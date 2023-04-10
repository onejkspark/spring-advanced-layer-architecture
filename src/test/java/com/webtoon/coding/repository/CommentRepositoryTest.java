package com.webtoon.coding.repository;

import com.webtoon.coding.config.JPAConfiguration;
import com.webtoon.coding.domain.comment.Comment;
import com.webtoon.coding.domain.content.Contents;
import com.webtoon.coding.domain.user.User;
import com.webtoon.coding.mock.CommentMock;
import com.webtoon.coding.mock.ContentsMock;
import com.webtoon.coding.mock.UserMock;
import com.webtoon.coding.infra.repository.comment.CommentRepository;
import com.webtoon.coding.infra.repository.contents.ContentsRepository;
import com.webtoon.coding.infra.repository.user.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import(JPAConfiguration.class)
class CommentRepositoryTest {

  @Autowired private UserRepository userRepository;

  @Autowired private ContentsRepository contentsRepository;
  @Autowired private CommentRepository commentRepository;

  private User user;

  private Contents contents;

  @BeforeEach
  void init() {
    user = userRepository.saveAndFlush(UserMock.createdMock());
    contents = contentsRepository.saveAndFlush(ContentsMock.createdMock());
  }

  @Test
  @Disabled("단일 작품 평가 저장")
  void save() {

    Comment mock = CommentMock.createdMock(user, contents);

    Comment entity = commentRepository.save(mock);

    commentRepository.flush();

    org.assertj.core.api.Assertions.assertThat(entity).isEqualTo(mock);

    Assertions.assertEquals(entity.getId().getUserId(), mock.getUser().getId());
    Assertions.assertEquals(entity.getId().getContentsId(), mock.getId().getContentsId());
    Assertions.assertEquals(entity.getComment(), mock.getComment());
  }

  @Test
  @DisplayName("유니크 키 테스트 케이스")
  void save_overlap() {

    Comment mock1 = CommentMock.createdMock(user, contents);

    commentRepository.save(mock1);

    commentRepository.flush();

    Comment mock2 = CommentMock.createdMock(user, contents);

    commentRepository.save(mock2);

    commentRepository.flush();
  }

  @Nested
  class Delete {

    private Comment mock;

    @BeforeEach
    void init() {
      mock = commentRepository.save(CommentMock.createdMock(user, contents));
      commentRepository.flush();
    }

    @Test
    @DisplayName("평가 삭제 로직 테스트 케이스")
    void deleteById_UserId() {
      commentRepository.deleteById_UserId(user.getId());
      commentRepository.flush();
    }
  }
}
