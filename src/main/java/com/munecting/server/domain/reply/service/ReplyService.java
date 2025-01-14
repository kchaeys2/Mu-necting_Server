package com.munecting.server.domain.reply.service;
import com.munecting.server.domain.archive.entity.Archive;
import com.munecting.server.domain.archive.repository.ArchiveRepository;
import com.munecting.server.domain.member.entity.Member;
import com.munecting.server.domain.reply.entity.Reply;
import com.munecting.server.domain.reply.repository.ReplyRepository;
import com.munecting.server.domain.member.repository.MemberRepository;
import com.munecting.server.domain.reply.dto.post.ReplyRequestDTO;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final ArchiveRepository archiveRepository;
    private final MemberRepository memberRepository;

    public ReplyService(ReplyRepository replyRepository, ArchiveRepository archiveRepository,MemberRepository memberRepository) {
        this.replyRepository = replyRepository;
        this.archiveRepository = archiveRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void reply(Long archiveId, ReplyRequestDTO replyRequest) {
        Long memberId = replyRequest.getMemberId();
        Optional<Member> member = memberRepository.findById(memberId);

        Archive archive = archiveRepository.findArchiveById(archiveId);

        //중복 방지
        if (replyRepository.existsByMemberIdAndArchiveId(member.get(), archive)) {
            throw new IllegalArgumentException("Reply already exists.");
        }

        Reply reply = new Reply();
        reply.setMemberId(member.get());
        reply.setStatus("REPLIED");
        reply.setArchiveId(archive);

        // Reply 저장
        replyRepository.save(reply);
        archive.increaseReplyCnt(); // replyCnt 증가
        archiveRepository.save(archive);

        updateReplyTotalCnt(member.get().getId());

    }

    @Transactional
    public void updateReplyTotalCnt(Long archiveId) {
        Archive archive = archiveRepository.findArchiveById(archiveId);

        Long memberId = archive.getMemberId().getId();
        Optional<Member> member = memberRepository.findById(memberId);

        List<Archive> archivesWithSameMember = archiveRepository.findAllByMemberId(member.get());
        int replyTotalCnt = archivesWithSameMember.stream()
                .mapToInt(Archive::getReplyCnt)
                .sum();

        member.get().setReplyTotalCnt(replyTotalCnt);
        memberRepository.save(member.get());
    }

//reply 개수 조회
@Transactional
    public int getReplyCount(Long archiveId) {
        Archive archive = archiveRepository.findArchiveById(archiveId);
        if (archive == null) {
            throw new DataRetrievalFailureException("Archive not found with id: " + archiveId);
        }
        return archive.getReplyCnt();
    }


    @Transactional
    public void unreply(Long archiveId, Long memberId) {
        Archive archive = archiveRepository.findArchiveById(archiveId);
        Optional<Member> member = memberRepository.findById(memberId);


        Reply reply = replyRepository.findByMemberIdAndArchiveId(member.get(), archive)
                .orElseThrow(() -> new DataRetrievalFailureException("Reply not found for member and archive"));

        // Reply 삭제
        replyRepository.delete(reply);

        // archive의 replyCnt 감소
        archive.decreaseReplyCnt();
        archiveRepository.save(archive);

        updateReplyTotalCnt(member.get().getId());
    }

}


