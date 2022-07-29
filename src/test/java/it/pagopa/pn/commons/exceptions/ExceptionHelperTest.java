package it.pagopa.pn.commons.exceptions;

import it.pagopa.pn.common.rest.error.v1.dto.Problem;
import it.pagopa.pn.commons.exceptions.dto.ProblemError;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionHelperTest {


    @Test
    void handlePnInternalException() {

        //When
        Problem res = ExceptionHelper.handleException(new PnInternalException("some message"));

        //Then
        assertNotNull(res);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), res.getTitle());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), res.getStatus());
        assertNotNull(res.getTimestamp());
        assertNotNull(res.getErrors());
        assertEquals(ExceptionHelper.ERROR_CODE_GENERIC_ERROR, res.getErrors().get(0).getCode());
    }


    @Test
    void handlePnRuntimeException() {

        //When
        Problem res = ExceptionHelper.handleException(new PnRuntimeException("some message", "some desc", 404, "NOT_FOUND", null,null));

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
        Problem res = ExceptionHelper.handleException(new PnRuntimeException("some message", "some desc", 404, new ArrayList<ProblemError>(), null));

        //Then
        assertNotNull(res);
        assertEquals("some message", res.getTitle());
        assertEquals(404, res.getStatus());
        assertNotNull(res.getTimestamp());
        assertNotNull(res.getErrors());
        assertEquals( ExceptionHelper.ERROR_CODE_GENERIC_ERROR, res.getErrors().get(0).getCode());
    }


    @Test
    void handlePnValidationException() {
        // Given
        Set<ConstraintViolation> constraintViolations = new HashSet<>();
        constraintViolations.add(generateConstraintViolation("some message1", "param1"));
        constraintViolations.add(generateConstraintViolation("some message2", "param2"));

        //When
        Problem res = ExceptionHelper.handleException(new PnValidationException("some message", constraintViolations));

        //Then
        assertNotNull(res);
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), res.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), res.getStatus());
        assertNotNull(res.getTimestamp());
        assertNotNull(res.getErrors());
        assertEquals(2, res.getErrors().size());



        assertEquals(ExceptionHelper.ERROR_CODE_INVALID_PARAMETER, res.getErrors().get(0).getCode());
        assertEquals(ExceptionHelper.ERROR_CODE_INVALID_PARAMETER, res.getErrors().get(1).getCode());

        assertEquals(List.of("param1", "param2"), res.getErrors().stream().map(it.pagopa.pn.common.rest.error.v1.dto.ProblemError::getElement).sorted().collect(Collectors.toList()));
        assertEquals(List.of("some message1", "some message2"), res.getErrors().stream().map(it.pagopa.pn.common.rest.error.v1.dto.ProblemError::getDetail).sorted().collect(Collectors.toList()));

    }

    @Test
    void generateProblemErrorsFromConstraintViolation() {
        Set<ConstraintViolation> constraintViolations = new HashSet<>();
        constraintViolations.add(generateConstraintViolation("some message1", "param1"));
        constraintViolations.add(generateConstraintViolation("some message2", "param2"));

        List<ProblemError> res = ExceptionHelper.generateProblemErrorsFromConstraintViolation(constraintViolations);
        assertEquals(ExceptionHelper.ERROR_CODE_INVALID_PARAMETER, res.get(0).getCode());
        assertEquals(ExceptionHelper.ERROR_CODE_INVALID_PARAMETER, res.get(1).getCode());

        assertEquals(List.of("param1", "param2"), res.stream().map(ProblemError::getElement).sorted().collect(Collectors.toList()));
        assertEquals(List.of("some message1", "some message2"), res.stream().map(ProblemError::getDetail).sorted().collect(Collectors.toList()));

    }

    private ConstraintViolation generateConstraintViolation(String message, String element)
    {
        return  new ConstraintViolationImpl(message, new Path() {
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