package com.munecting.server.domain.music.entity;

import com.munecting.server.domain.BaseEntity;
import com.munecting.server.domain.archive.entity.Archive;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "music")
public class Music extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_id")
    private Long id;
    private String name;
    private String coverImg;
    private String musicPre; //spotify 30s
    @Column(name = "music_full")
    private String musicFull; // YouTube link full
    public void setMusicFull(String musicFull) {
        this.musicFull = musicFull;
    }
    @Enumerated(EnumType.STRING)
    private MusicGenre genre;
    private String artist;
    private String status="ACTIVE";
    @OneToOne(mappedBy = "musicId")
    private Archive archives;
    public Music(String name,String coverImg,String musicPre,String musicFull,MusicGenre genre,String artist){
        this.name = name;
        this.coverImg = coverImg;
        this.musicPre = musicPre;
        this.musicFull = musicFull;
        this.genre = genre;
        this.artist = artist;
    }
    //test
    public Music(String coverImg){
        this.coverImg = coverImg;
    }
}