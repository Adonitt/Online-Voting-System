package org.example.kqz.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.kqz.dtos.parties.CRDPartyRequestDto;
import org.example.kqz.dtos.parties.PartyDetailsDto;
import org.example.kqz.dtos.parties.PartyListingDto;
import org.example.kqz.dtos.parties.UpdatePartyDto;
import org.example.kqz.services.interfaces.PartyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/parties")
public class PartyController {
    private final PartyService service;

    @GetMapping("")
    public ResponseEntity<List<PartyListingDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartyDetailsDto> findById(@PathVariable Long id) {
        PartyDetailsDto party = service.findById(id);
        return ResponseEntity.ok(party);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CRDPartyRequestDto> create(@RequestPart("data") @Valid @ModelAttribute CRDPartyRequestDto dto,
                                                     @RequestPart(value = "symbol", required = false) MultipartFile symbol
    ) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto, symbol));
    }


    @PutMapping("/{id}")
    public ResponseEntity<UpdatePartyDto> modify(
            @PathVariable Long id,
            @RequestPart("data") @Valid UpdatePartyDto dto,
            @RequestPart(value = "symbol", required = false) MultipartFile symbol
    ) {
        return ResponseEntity.ok(service.modify(dto, id, symbol));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Long id) {
        service.removeById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/default")
    public CRDPartyRequestDto getDefault() {
        return new CRDPartyRequestDto();
    }
}
