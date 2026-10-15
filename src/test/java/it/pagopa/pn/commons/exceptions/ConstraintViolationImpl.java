package it.pagopa.pn.commons.exceptions;

import jakarta.validation.ConstraintTarget;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.Path;
import jakarta.validation.Payload;
import jakarta.validation.metadata.ConstraintDescriptor;
import jakarta.validation.metadata.ValidateUnwrappedValue;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConstraintViolationImpl< T > implements ConstraintViolationTest {

    private final String message;
    private final Path path;
    private final Annotation annotation;

    public ConstraintViolationImpl(String message, Class annotationclazz, Path path) {
        this.message = message;
        this.path = path;
        this.annotation = new Annotation() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return annotationclazz;
            }
        };
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getMessageTemplate() {
        return null;
    }

    @Override
    public T getRootBean() {
        return null;
    }

    @Override
    public Class<Object> getRootBeanClass() {
        return null;
    }

    @Override
    public Object getLeafBean() {
        return null;
    }

    @Override
    public Object[] getExecutableParameters() {
        return new Object[0];
    }

    @Override
    public Object getExecutableReturnValue() {
        return null;
    }

    @Override
    public Path getPropertyPath() {
        return path;
    }

    @Override
    public Object getInvalidValue() {
        return null;
    }

    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return new ConstraintDescriptor<Annotation>() {
            @Override
            public Annotation getAnnotation() {
                return annotation;
            }

            @Override
            public String getMessageTemplate() {
                return null;
            }

            @Override
            public Set<Class<?>> getGroups() {
                return null;
            }

            @Override
            public Set<Class<? extends Payload>> getPayload() {
                return null;
            }

            @Override
            public ConstraintTarget getValidationAppliesTo() {
                return null;
            }

            @Override
            public List<Class<? extends ConstraintValidator<Annotation, ?>>> getConstraintValidatorClasses() {
                return null;
            }

            @Override
            public Map<String, Object> getAttributes() {
                return null;
            }

            @Override
            public Set<ConstraintDescriptor<?>> getComposingConstraints() {
                return null;
            }

            @Override
            public boolean isReportAsSingleViolation() {
                return false;
            }

            @Override
            public ValidateUnwrappedValue getValueUnwrapping() {
                return null;
            }

            @Override
            public <U> U unwrap(Class<U> aClass) {
                return null;
            }
        };
    }

    @Override
    public <U> U unwrap(Class<U> aClass) {
        return null;
    }
}
