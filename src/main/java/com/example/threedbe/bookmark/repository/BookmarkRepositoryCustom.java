package com.example.threedbe.bookmark.repository;

import java.util.Optional;

import com.example.threedbe.bookmark.domain.Bookmark;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.post.domain.Post;

public interface BookmarkRepositoryCustom {

	Optional<Bookmark> findFirstByMemberAndPost(Member member, Post post);

}
