package br.com.campanha.controller;

import br.com.campanha.exception.RecursoNaoEncontradoException;
import br.com.campanha.exception.TimeNaoCadastradoException;
import br.com.campanha.model.ErrorInfo;
import br.com.campanha.model.Time;
import br.com.campanha.service.TimeService;
import br.com.campanha.vo.TimeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping("/v1/time")
@Api(value = "time", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE,
        tags = {"Endpoints do Time"})
public class TimeTorcedorController {

    private static final Logger LOGGER = LogManager.getLogger(TimeTorcedorController.class);

    private final TimeService timeService;

    public TimeTorcedorController(TimeService timeService) {
        this.timeService = timeService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Cria um novo time com base nos parametros passados",
            notes = "Cria um novo time e retorna o link do caminho no header",
            response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public ResponseEntity<?> cadastrarTime(@Valid @RequestBody TimeVO timeVO) {

        Time time = timeService.salvarTimeTorcedor(timeVO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(time.getId()).toUri();

        return created(location).build();
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Busca todos os times cadastrados",
            response = TimeVO.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public ResponseEntity<List<TimeVO>> buscarTodosOsTimes() {

        List<TimeVO> timeVOList = timeService.buscarTodos().stream()
                .map(TimeVO::new)
                .collect(Collectors.toList());

        timeVOList.forEach(timeVO -> timeVO.add(linkTo(methodOn(TimeTorcedorController.class).buscarPorId(timeVO.getChave())).withSelfRel()));

        return new ResponseEntity<>(timeVOList, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Busca o time por id",
            notes = "Retorna o time por ID",
            response = TimeVO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public ResponseEntity<TimeVO> buscarPorId(@PathVariable Integer id) {
        Optional<Time> timeOptional = timeService.buscarPorId(id);

        if (timeOptional.isPresent()) {
            TimeVO timeVO = new TimeVO(timeOptional.get());
            timeVO.add(linkTo(methodOn(TimeTorcedorController.class).buscarPorId(timeVO.getChave())).withSelfRel());
            return new ResponseEntity<>(timeVO, HttpStatus.OK);
        }

        LOGGER.info("O time com ID: {} não foi encontrada", id);
        throw new RecursoNaoEncontradoException();
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Deleta time por id",
            notes = "Deleta o Time por ID idependente",
            response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public ResponseEntity<?> deletarPorId(@PathVariable Integer id) {

        timeService.deletarPorId(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    ErrorInfo handleInternalServerError(Exception ex) {
        return new ErrorInfo(ServletUriComponentsBuilder.fromCurrentRequest().path("").toUriString(), ex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    ErrorInfo
    handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return new ErrorInfo(ServletUriComponentsBuilder.fromCurrentRequest().path("").toUriString(), ex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    ErrorInfo
    handleValidationException(MethodArgumentNotValidException ex) {
        return new ErrorInfo(ServletUriComponentsBuilder.fromCurrentRequest().path("").toUriString(), ex);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({RecursoNaoEncontradoException.class, TimeNaoCadastradoException.class})
    @ResponseBody
    ErrorInfo
    handleNotFoundException(RecursoNaoEncontradoException ex) {
        return new ErrorInfo(ServletUriComponentsBuilder.fromCurrentRequest().path("").toUriString(), ex);
    }
}
