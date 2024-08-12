package likelion.eight.domain.bootcamp.infra;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import likelion.eight.bootcamp.BootCampEntity;
import likelion.eight.bootcamp.QBootCampEntity;
import likelion.eight.domain.bootcamp.converter.BootcampConverter;
import likelion.eight.domain.bootcamp.model.Bootcamp;
import likelion.eight.domain.bootcamp.service.port.BootcampRepositoryQueryDsl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BootcampRepositoryQueryDSLImpl implements BootcampRepositoryQueryDsl {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Bootcamp> findByName(String name) {
        QBootCampEntity bootCampEntity = QBootCampEntity.bootCampEntity;

        List<BootCampEntity> bootCampEntities = queryFactory.select(bootCampEntity)
                .from(bootCampEntity)
                .where(nameLike(name))
                .orderBy(bootCampEntity.registrationDate.desc()) // 최신순 정렬
                .fetch();

        return bootCampEntities.stream()
                .map(BootcampConverter::toBootcamp)
                .collect(Collectors.toList());

    }

    private BooleanExpression nameLike(String name){
        if (name == null || name.isEmpty()){
            return null;
        }

        return QBootCampEntity.bootCampEntity.name.containsIgnoreCase(name);
    }
}
