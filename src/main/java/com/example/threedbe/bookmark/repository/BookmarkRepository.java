package com.example.threedbe.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.threedbe.bookmark.domain.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {

}
