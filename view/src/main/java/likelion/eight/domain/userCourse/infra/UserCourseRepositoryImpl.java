package likelion.eight.domain.userCourse.infra;

import likelion.eight.course.CourseEntity;
import likelion.eight.domain.course.converter.CourseConverter;
import likelion.eight.domain.course.model.Course;
import likelion.eight.domain.user.converter.UserConverter;
import likelion.eight.domain.user.model.User;
import likelion.eight.domain.userCourse.converter.UserCourseConverter;
import likelion.eight.domain.userCourse.model.UserCourse;
import likelion.eight.domain.userCourse.service.port.UserCourseRepository;
import likelion.eight.user.UserEntity;
import likelion.eight.userCourse.UserCourseEntity;
import likelion.eight.userCourse.ifs.UserCourseJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserCourseRepositoryImpl implements UserCourseRepository {

    private final UserCourseJpaRepository jpaRepository;

    @Override
    public UserCourse save(UserCourseEntity userCourseEntity) {
        UserCourseEntity savedUserCourseEntity = jpaRepository.save(userCourseEntity);
        UserCourse userCourse = UserCourseConverter.toUserCourse(savedUserCourseEntity);

        return userCourse;    }


    @Override
    public List<Long> findCourseIdsByUserId(Long userId) {
        List<UserCourseEntity> userCourses = jpaRepository.findByUserEntity_Id(userId);
        return userCourses.stream()
                .map(userCourse -> userCourse.getCourseEntity().getId())
                .collect(Collectors.toList());    }

    @Override
    public Optional<UserCourseEntity> findByUserEntityAndCourseEntity(UserEntity userEntity, CourseEntity courseEntity) {
        return jpaRepository.findByUserEntityAndCourseEntity(userEntity, courseEntity);

    }
}
