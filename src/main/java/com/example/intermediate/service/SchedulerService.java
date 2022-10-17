package com.example.intermediate.service;

import com.example.intermediate.domain.Post;
import com.example.intermediate.repository.CommentRepository;
import com.example.intermediate.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j //로그
@RequiredArgsConstructor // final 멤버 변수를 자동으로 생성
@Component // 스프링이 필요 시 자동으로 생성하는 클래스 목록에 추가
public class SchedulerService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 현재날짜 및 시간
    public String getNowDateTime24() {
        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy.MM.dd kk:mm:ss E요일");
        String str = dayTime.format(new Date(time));

        return str;
    }

    // 초, 분, 시, 일, 월, 주 순서
    @Scheduled(cron = "0 00 01 * * *")
    public void selectPost() {
        System.out.println("[JopTime] : " + getNowDateTime24());
        System.out.println("게시글 조회");

        List<Post> postList = postRepository.findAll();

        for (Post post : postList) {
            if(commentRepository.findAllByPost(post).isEmpty()) {
                log.info("게시물 < " + post.getTitle() + " > 이 삭제되었습니다.");
                postRepository.deleteById(post.getId());
            }
        }
    }
}