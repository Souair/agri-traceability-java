package com.shaoyang.traceability.controller;

import com.shaoyang.traceability.domain.dto.TraceChainResponse;
import com.shaoyang.traceability.domain.enums.TraceStage;
import com.shaoyang.traceability.service.TraceabilityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    private final TraceabilityService traceabilityService;

    public PageController(TraceabilityService traceabilityService) {
        this.traceabilityService = traceabilityService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("stages", TraceStage.values());
        return "admin";
    }

    @GetMapping("/trace/{qrCode}")
    public String trace(@PathVariable String qrCode, Model model) {
        TraceChainResponse response = traceabilityService.queryByQrCode(qrCode);
        model.addAttribute("result", response);
        return "trace";
    }
}
