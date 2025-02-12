package com.munecting.server.domain.music.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.munecting.server.domain.archive.service.ArchiveService;
import com.munecting.server.domain.member.entity.Member;
import com.munecting.server.domain.music.dto.get.MusicSearchPageRes;
import com.munecting.server.domain.music.dto.post.UploadMusicReq;
import com.munecting.server.domain.music.entity.Music;
import com.munecting.server.domain.music.service.MusicService;
import com.munecting.server.domain.music.service.YoutubeService;
import com.munecting.server.global.config.BaseResponse;
import com.munecting.server.global.config.BaseResponseStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/musics")
@RequiredArgsConstructor
@Slf4j
public class MusicController {
    private final MusicService musicService;
    private final ArchiveService archiveService;

    private final YoutubeService youtubeService;

    //음악 검색
    @ResponseBody
    @GetMapping("")
    public BaseResponse<MusicSearchPageRes> getMusicSearch(String search, int page){
        return new BaseResponse<>(musicService.getMusicSearch(search,page));
    }
    //아카이브 업로드
    @ResponseBody
    @PostMapping("")
    public BaseResponse postMusicArchive(@RequestBody UploadMusicReq uploadMusicReq, HttpServletRequest memberId) throws Exception{
        archiveService.saveArchive(uploadMusicReq,musicService.saveMusic(uploadMusicReq),memberId);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

    @PostMapping("/youtube-fulllink")
    public ResponseEntity<?> searchMusic(@RequestParam String name, @RequestParam String artist) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(artist)) {
            return ResponseEntity.badRequest().body("Song name and artist must not be empty");
        }

        String songLink = youtubeService.getSongLink(name, artist);
        if (songLink != null) {
            return ResponseEntity.ok(songLink);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Song not found");
        }
    }

}


