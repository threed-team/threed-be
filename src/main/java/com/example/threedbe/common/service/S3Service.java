package com.example.threedbe.common.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.threedbe.common.exception.ThreedServerErrorException;
import com.example.threedbe.post.dto.response.PresignedUrlResponse;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	@Value("${aws.s3.thumbnail-directory}")
	private String thumbnailDirectory;

	@Value("${aws.s3.cdn-url}")
	private String cdnUrl;

	private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofMinutes(5);
	private static final String IMAGE_FORMAT = "png";

	public String uploadThumbnailImage(BufferedImage image, String title) {
		try {
			String fileName = createSafeFileName(title);
			String key = generateThumbnailKey(fileName + "." + IMAGE_FORMAT);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ImageIO.write(image, IMAGE_FORMAT, outputStream);
			byte[] imageBytes = outputStream.toByteArray();

			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.contentType(String.format("image/%s", IMAGE_FORMAT))
				.build();

			s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));

			return createFileUrl(key);
		} catch (IOException e) {
			throw new ThreedServerErrorException("이미지 업로드에 실패했습니다.");
		}
	}

	public void deleteThumbnail(String thumbnailUrl) {
		try {
			String key = extractKeyFromUrl(thumbnailUrl);

			DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();

			s3Client.deleteObject(deleteObjectRequest);
		} catch (Exception e) {
			throw new ThreedServerErrorException("이미지 삭제에 실패했습니다.");
		}
	}

	public PresignedUrlResponse generatePresignedUrl(Long postId, String fileName) {
		String filePath = generateImageFilePath(postId, fileName);
		PutObjectPresignRequest presignRequest = buildPresignedRequest(filePath);
		String presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString();
		String fileUrl = createFileUrl(filePath);

		return new PresignedUrlResponse(presignedUrl, fileUrl);
	}

	private String generateThumbnailKey(String originalFilename) {
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String fileNameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
		String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));

		return thumbnailDirectory + fileNameWithoutExtension + "_" + timestamp + extension;
	}

	private String extractKeyFromUrl(String url) {
		return url.substring(cdnUrl.length() + 1);
	}

	private String createSafeFileName(String title) {
		if (title == null || title.trim().isEmpty()) {
			return "thumbnail";
		}

		String safeFileName = title.replaceAll("[^a-zA-Z0-9가-힣\\s]", "")
			.replaceAll("\\s+", "_")
			.toLowerCase();

		if (safeFileName.isEmpty()) {
			return "thumbnail";
		}

		if (safeFileName.length() > 50) {
			safeFileName = safeFileName.substring(0, 50);
		}

		return safeFileName;
	}

	private String createFileUrl(String filePath) {
		URI domain = URI.create(cdnUrl);

		return domain.resolve(filePath).toString();
	}

	private PutObjectPresignRequest buildPresignedRequest(String filePath) {
		return PutObjectPresignRequest.builder()
			.signatureDuration(PRESIGNED_URL_EXPIRATION)
			.putObjectRequest(putObjectRequest -> putObjectRequest.bucket(bucketName)
				.key(filePath))
			.build();
	}

	private String generateImageFilePath(Long postId, String fileName) {
		return String.format(
			"posts/%d/images/%s.%s",
			postId,
			UUID.randomUUID(),
			getFileExtension(fileName));
	}

	private String getFileExtension(String fileName) {
		int lastDotIndex = fileName.lastIndexOf('.');

		return lastDotIndex == -1 ? "" : fileName.substring(lastDotIndex + 1).toLowerCase();
	}

}
