//package com.optimize.common.entities.annotations.validator;
//
//import com.optimize.common.entities.annotations.ExistsInDB;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import org.springframework.stereotype.Component;
//
//import java.util.Objects;
//
//@Component
//public class ExistsInDBValidator implements ConstraintValidator<ExistsInDB, String> {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    private Class<?> entity;
//    private String field;
//
//    @Override
//    public void initialize(ExistsInDB constraintAnnotation) {
//        this.entity = constraintAnnotation.entity();
//        this.field = constraintAnnotation.field();
//    }
//
//    @Override
//    public boolean isValid(String value, ConstraintValidatorContext context) {
//        if (value == null || value.isEmpty()) {
//            return true; // Considérez une valeur nulle comme valide si nécessaire
//        }
//        String jpql1 = String.format("SELECT COUNT(e) FROM %s e",
//                entity.getSimpleName());
//
//        if (Objects.nonNull(entityManager)) {
//            Long count1 = (Long) entityManager.createQuery(jpql1)
//                    .getSingleResult();
//
//            if (count1 == 0) {
//                return true;
//            }
//
//            String jpql = String.format("SELECT COUNT(e) FROM %s e WHERE e.%s = :value",
//                    entity.getSimpleName(), field);
//
//            Long count = (Long) entityManager.createQuery(jpql)
//                    .setParameter("value", value)
//                    .getSingleResult();
//
//            return count > 0; // Retourne true si la valeur existe
//        } else {
//            return true;
//        }
//
//    }
//}
