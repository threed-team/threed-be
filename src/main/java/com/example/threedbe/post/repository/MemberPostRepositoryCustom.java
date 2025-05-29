package com.example.threedbe.post.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.example.threedbe.post.domain.MemberPost;

public interface MemberPostRepositoryCustom {

	List<MemberPost> findPopularPosts(LocalDateTime publishedAfter);

}
