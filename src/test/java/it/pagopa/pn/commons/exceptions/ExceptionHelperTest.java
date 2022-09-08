package it.pagopa.pn.commons.exceptions;

import it.pagopa.pn.common.rest.error.v1.dto.Problem;
import it.pagopa.pn.commons.exceptions.dto.ProblemError;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import javax.validation.*;
import javax.validation.constraints.Max;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExceptionHelperTest {


    ExceptionHelper exceptionHelper;

    @BeforeEach
    public void init(){
        this.exceptionHelper = new ExceptionHelper(Optional.empty());
    }


    @Test
    void handlePnInternalException() {

        //When
        Problem res = exceptionHelper.handleException(new PnInternalException("some message"));

        //Then
        assertNotNull(res);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), res.getTitle());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), res.getStatus());
        assertNotNull(res.getTimestamp());
        assertNotNull(res.getErrors());
        assertEquals(PnExceptionsCodes.ERROR_CODE_PN_GENERIC_ERROR, res.getErrors().get(0).getCode());
    }


    @Test
    void handlePnRuntimeException() {

        //When
        Problem res = exceptionHelper.handleException(new PnRuntimeException("some message", "some desc", 404, "NOT_FOUND", null,null));

        //Then
        assertNotNull(res);
        assertEquals("some message", res.getTitle());
        assertEquals(404, res.getStatus());
        assertNotNull(res.getTimestamp());
        assertNotNull(res.getErrors());
        assertEquals( "NOT_FOUND", res.getErrors().get(0).getCode());
    }


    @Test
    void handlePnRuntimeException1() {

        //When
        Problem res = exceptionHelper.handleException(new PnRuntimeException("some message", "some desc", 404, new ArrayList<ProblemError>(), null));

        //Then
        assertNotNull(res);
        assertEquals("some message", res.getTitle());
        assertEquals(404, res.getStatus());
        assertNotNull(res.getTimestamp());
        assertNotNull(res.getErrors());
        assertEquals( PnExceptionsCodes.ERROR_CODE_PN_GENERIC_ERROR, res.getErrors().get(0).getCode());
    }


    @Test
    void handlePnValidationException() {
        // Given
        TestValidation val = new TestValidation();
        val.innerobjects = new TestValidation[2];
        val.innerobjects[0] = new TestValidation();
        val.innerobjects[0].notnullvalue = "valido";
        val.innerobjects[1] = new TestValidation();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<TestValidation>> constraintViolations =  validator.validate(val);
        //When
        Problem res = exceptionHelper.handleException(new PnValidationException("some message", constraintViolations));

        //Then
        assertNotNull(res);
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), res.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), res.getStatus());
        assertNotNull(res.getTimestamp());
        assertNotNull(res.getErrors());
        assertEquals(2, res.getErrors().size());



        assertEquals(List.of(PnExceptionsCodes.ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_REQUIRED, PnExceptionsCodes.ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_REQUIRED), res.getErrors().stream().map(it.pagopa.pn.common.rest.error.v1.dto.ProblemError::getCode).sorted().collect(Collectors.toList()));
        assertEquals(List.of("innerobjects[1].notnullvalue", "notnullvalue"), res.getErrors().stream().map(it.pagopa.pn.common.rest.error.v1.dto.ProblemError::getElement).sorted().collect(Collectors.toList()));

    }

    @Test
    void generateProblemErrorsFromConstraintViolationRealValidation() {
        TestValidation val = new TestValidation();
        val.innerobjects = new TestValidation[2];
        val.innerobjects[0] = new TestValidation();
        val.innerobjects[0].notnullvalue = "valido";
        val.innerobjects[1] = new TestValidation();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<TestValidation>> constraintViolations =  validator.validate(val);

        List<ProblemError> res = exceptionHelper.generateProblemErrorsFromConstraintViolation(constraintViolations);

        assertEquals(List.of(PnExceptionsCodes.ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_REQUIRED,PnExceptionsCodes.ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_REQUIRED), res.stream().map(ProblemError::getCode).sorted().collect(Collectors.toList()));
        assertEquals(List.of("innerobjects[1].notnullvalue", "notnullvalue"), res.stream().map(ProblemError::getElement).sorted().collect(Collectors.toList()));

    }

    private class TestValidation{
        public @javax.validation.constraints.NotNull String notnullvalue;
        @Valid
        public TestValidation[] innerobjects;
    }

    private ConstraintViolation generateConstraintViolation(String message, String element, Class annotationClass)
    {
        return  new ConstraintViolationImpl(message, annotationClass, new Path() {
            @NotNull
            @Override
            public Iterator<Node> iterator() {
                Node n = new Node() {
                    @Override
                    public String getName() {
                        return element;
                    }

                    @Override
                    public boolean isInIterable() {
                        return false;
                    }

                    @Override
                    public Integer getIndex() {
                        return null;
                    }

                    @Override
                    public Object getKey() {
                        return null;
                    }

                    @Override
                    public ElementKind getKind() {
                        return null;
                    }

                    @Override
                    public <T extends Node> T as(Class<T> aClass) {
                        return null;
                    }
                };
                return List.of(n).iterator();
            }
        });
    }
}