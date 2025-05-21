package com.example.threedbe.common.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.threedbe.common.exception.ThreedServerErrorException;
import com.example.threedbe.post.service.ThumbnailService;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Client s3Client;
	private final ThumbnailService thumbnailService;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	@Value("${aws.s3.thumbnail-directory}")
	private String thumbnailDirectory;

	@Value("${aws.s3.cdn-url}")
	private String cdnUrl;

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
				.contentType("image/" + IMAGE_FORMAT)
				.build();

			s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));

			return String.format("%s/%s", cdnUrl, key);
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

	private String generateThumbnailKey(String originalFilename) {
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String fileNameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
		String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));

		return thumbnailDirectory + fileNameWithoutExtension + "_" + timestamp + extension;
	}

	private String extractKeyFromUrl(String url) {

		return url.substring(cdnUrl.length() + 1);
	}

	public String createSafeFileName(String title) {
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

}
