package com.webtoon.coding.service;

import com.webtoon.coding.domain.comment.Comment;
import com.webtoon.coding.domain.comment.CommentWriter;
import com.webtoon.coding.domain.contents.Contents;
import com.webtoon.coding.domain.core.Reader;
import com.webtoon.coding.domain.core.Verifier;
import com.webtoon.coding.domain.user.User;
import com.webtoon.coding.dto.request.ContentsCommentRequest;
import com.webtoon.coding.exception.MsgType;
import com.webtoon.coding.exception.NoDataException;
import com.webtoon.coding.mock.CommentMock;
import com.webtoon.coding.mock.ContentsMock;
import com.webtoon.coding.mock.UserMock;
import com.webtoon.coding.service.comment.CommentService;
import com.webtoon.coding.service.comment.CommentServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentServiceTest {

    private CommentService commentService;

    @Mock
    private Reader<Contents> contentsReader;

    @Mock
    private Reader<User> userReader;

    @Mock
    private CommentWriter commentWriter;

    @Mock
    private Verifier<Comment> commentVerifier;

    @BeforeEach
    void init() {
        this.commentService = new CommentServiceImpl(
                commentVerifier,
                contentsReader,
                userReader,
                commentWriter
        );
    }

    @Nested
    @DisplayName("생성 메소드는")
    public class CreatedMethod {

        @Test
        @DisplayName("성공적으로 실행이 된다.")
        void testCreatedSuccess() {

            User user = UserMock.createdMock();

            Contents contents = ContentsMock.createdMock();

            Comment mock = CommentMock.createdMock(user, contents);

            when(userReader.get(any())).thenReturn(user);

            when(contentsReader.get(any())).thenReturn(contents);

            when(commentWriter.save(any())).thenReturn(mock);

            ContentsCommentRequest dto = CommentMock.createdStoreDTO();

            Comment entity = commentService.created(dto);

            verify(userReader, times(1)).get(any());

            verify(contentsReader, times(1)).get(any());

            verify(commentWriter, times(1)).save(any());

            org.assertj.core.api.Assertions.assertThat(entity).isEqualTo(mock);

            org.junit.jupiter.api.Assertions.assertEquals(
                    entity.getId().getUserId(), mock.getUser().getId());
            org.junit.jupiter.api.Assertions.assertEquals(
                    entity.getId().getContentsId(), mock.getId().getContentsId());
            org.junit.jupiter.api.Assertions.assertEquals(entity.getComment(), mock.getComment());
        }

        @Test
        @DisplayName("유저 데이터가 없다면 실패를 한다.")
        void testCreatedFailByNoUser() {

            User user = User.builder().build();

            Contents contents = ContentsMock.createdMock();

            Comment mock = CommentMock.createdMock(user, contents);

            when(userReader.get(any())).thenThrow(new NoDataException(MsgType.NoUserData));

            ContentsCommentRequest dto = CommentMock.createdStoreDTO();

            NoDataException e = Assertions.assertThrows(NoDataException.class, () -> commentService.created(dto));

            verify(userReader, times(1)).get(any());

            Assertions.assertEquals(e.getMsgType(), MsgType.NoUserData);
            Assertions.assertEquals(e.getMessage(), MsgType.NoUserData.getMessage());
        }

        @Test
        @DisplayName("컨텐츠 데이터가 없다면 실패를 한다.")
        void testCreatedFailByNoContents() {

            User user = UserMock.createdMock();

            Contents contents = ContentsMock.createdMock();

            Comment mock = CommentMock.createdMock(user, contents);

            when(userReader.get(any())).thenReturn(user);

            when(contentsReader.get(any())).thenThrow(new NoDataException(MsgType.NoContentsData));

            ContentsCommentRequest dto = CommentMock.createdStoreDTO();

            NoDataException e = Assertions.assertThrows(NoDataException.class, () -> commentService.created(dto));

            verify(userReader, times(0)).get(any());

            verify(contentsReader, times(1)).get(any());

            Assertions.assertEquals(e.getMsgType(), MsgType.NoContentsData);
            Assertions.assertEquals(e.getMessage(), MsgType.NoContentsData.getMessage());
        }


    }

}
