package br.com.campanha.controller;

import br.com.campanha.exception.RecursoNaoEncontradoException;
import br.com.campanha.exception.TimeNaoCadastradoException;
import br.com.campanha.model.Campanha;
import br.com.campanha.model.ErrorInfo;
import br.com.campanha.service.CampanhaService;
import br.com.campanha.vo.CampanhaVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping("/v1/campanhas")
@Api(value = "Campanha", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE,
        tags = {"Endpoints da Campanha"})
public class CampanhaController {

    private static final Logger LOGGER = LogManager.getLogger(CampanhaController.class);

    private final CampanhaService campanhaService;

    public CampanhaController(CampanhaService campanhaService) {
        this.campanhaService = campanhaService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Cria uma nova campanha com base nos parametros passados",
            notes = "Cria uma nova campanha e retorna o link do caminho no header",
            response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public ResponseEntity<?> cadastrarCampanha(@Valid @RequestBody CampanhaVO campanhaVO) {

        try {
            Campanha campanha = campanhaService.cadastrarCampanha(campanhaVO.getNome(), campanhaVO.getTimeCoracaoId(),
                    campanhaVO.getInicioVigencia(), campanhaVO.getFimVigencia());

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}")
                    .buildAndExpand(campanha.getId()).toUri();

            return created(location).build();
        } catch (NoSuchElementException ex) {
            LOGGER.error("Não existe time cadastrado para essa: {} campanha", campanhaVO);
            throw new TimeNaoCadastradoException();
        }
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Busca todos as campanhas ativas",
            notes = "Para retornar as Campanhas ativas é usado a data atual",
            response = CampanhaVO.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public ResponseEntity<List<CampanhaVO>> buscarTodasCampanhas() {

        List<CampanhaVO> campanhaVOList = campanhaService.buscarTodasAsCampanhasAtivas(LocalDate.now()).stream()
                .map(CampanhaVO::new)
                .collect(Collectors.toList());

        campanhaVOList.forEach(campanhaVO -> campanhaVO.add(linkTo(methodOn(CampanhaController.class).buscarPorId(campanhaVO.getChave())).withSelfRel()));

        return new ResponseEntity<>(campanhaVOList, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Busca a campanha por id",
            notes = "Retorna a campanha por ID idependente da data de vigência",
            response = CampanhaVO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public ResponseEntity<CampanhaVO> buscarPorId(@PathVariable Integer id) {
        Optional<Campanha> campanhaOptional = campanhaService.buscarPorId(id);

        if (campanhaOptional.isPresent()) {
            CampanhaVO campanhaVO = new CampanhaVO(campanhaOptional.get());
            campanhaVO.add(linkTo(methodOn(CampanhaController.class).buscarPorId(campanhaVO.getChave())).withSelfRel());
            return new ResponseEntity<>(campanhaVO, HttpStatus.OK);
        }

        LOGGER.error("A campanha com ID: {} não foi encontrada", id);
        throw new RecursoNaoEncontradoException();
    }

    @GetMapping(value = "/time-coracao/{timeCoracao}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Busca campanha por time do coração",
            notes = "Retorna a campanha por time do coração somente para campanhas ativas",
            response = CampanhaVO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public ResponseEntity<List<CampanhaVO>> buscaPorTimeDoCoracao(@PathVariable String timeCoracao) {

        try {
            List<CampanhaVO> campanhaVOList = campanhaService.buscaPorTimeDoCoracao(timeCoracao).stream()
                    .map(CampanhaVO::new)
                    .collect(Collectors.toList());

            campanhaVOList.forEach(campanhaVO -> campanhaVO.add(linkTo(methodOn(CampanhaController.class).buscarPorId(campanhaVO.getChave())).withSelfRel()));

            return new ResponseEntity<>(campanhaVOList, HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            LOGGER.error("Não existe time cadastrado para essa esse id: {}", timeCoracao);
            throw new RecursoNaoEncontradoException();
        }
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Deleta a campanha por id",
            notes = "Deleta a campanha por ID idependente da data de vigência",
            response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public ResponseEntity<?> deletarPorId(@PathVariable Integer id) {

        campanhaService.deletarPorId(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Atualiza a campanha por id",
            notes = "Atualiza a campanha por ID idependente da data de vigência",
            response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public ResponseEntity<?> atualizarCampanha(@PathVariable Integer id, @Valid @RequestBody CampanhaVO campanhaVO) {

        Optional<Campanha> campanhaOptional = campanhaService.buscarPorId(id);

        campanhaOptional.ifPresent(campanha -> {
            campanha.atualizarDados(campanhaVO);
            campanhaService.salvarCampanha(campanha);
        });

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