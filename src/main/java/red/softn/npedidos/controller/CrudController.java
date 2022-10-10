package red.softn.npedidos.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import red.softn.npedidos.exception.InternalServerErrorException;
import red.softn.npedidos.pojo.ErrorDetails;
import red.softn.npedidos.service.CrudServiceI;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.net.URI;

public abstract class CrudController<E, R, ID> {
    
    public abstract CrudServiceI<E, R, ID> getService();
    
    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(value = "f", required = false) String filter) {
        if (StringUtils.isBlank(filter)) {
            return ResponseEntity.ok(getService().findAll());
        }
        
        return ResponseEntity.ok(getService().findAll(filter));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable ID id) {
        return ResponseEntity.ok(getService().findById(id));
    }
    
    @PostMapping
    public ResponseEntity<?> save(@RequestBody E request, UriComponentsBuilder uriComponentsBuilder, HttpServletRequest httpServletRequest) {
        R save = getService().save(request);
        Object valueId = getValueId(save);
        URI uri = uriComponentsBuilder.path(httpServletRequest.getServletPath())
                                      .pathSegment("{id}")
                                      .buildAndExpand(valueId)
                                      .toUri();
        
        return ResponseEntity.created(uri)
                             .body(save);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable ID id, @RequestBody E request) {
        return ResponseEntity.ok(getService().update(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable ID id) {
        getService().delete(id);
        
        return ResponseEntity.noContent()
                             .build();
    }
    
    private Object getValueId(R response) {
        Field field = ReflectionUtils.findField(response.getClass(), "id");
        Object fieldId;
        
        if (field == null) {
            throw new InternalServerErrorException(new ErrorDetails("La clase no tiene una propiedad ID."));
        }
        
        try {
            ReflectionUtils.makeAccessible(field);
            fieldId = ReflectionUtils.getField(field, response);
        } catch (Exception ex) {
            throw new InternalServerErrorException(new ErrorDetails("Error al obtener el valor de la propiedad ID."));
        }
        
        return fieldId;
    }
    
}
