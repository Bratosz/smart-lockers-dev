package pl.bratosz.smartlockers.controller;

import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.files.TemplateTypeForPlantLoad;
import pl.bratosz.smartlockers.response.DownloadFileResponse;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.ReportService;

@RestController
@RequestMapping("/report")
public class ReportController {
    private ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/generate/{userId}")
    public DownloadFileResponse generate(@PathVariable long userId){
        return reportService.generateAllOrdersReport(userId);
    }

    @GetMapping("/generate-for-new-employees/{userId}")
    public DownloadFileResponse generateForNewEmployees(
            @PathVariable long userId) {
        return reportService.generateForNotReportedEmployeesToRelease(userId);
    }

    @GetMapping("/get-employees-to-measure-list/{userId}")
    public DownloadFileResponse getEmployeesToMeasureList(
            @PathVariable long userId) {
        return reportService.getEmployeesToMeasureList(userId);
    }

    @GetMapping("/get-template-for-create-client/{templateType}/{userId}")
    public DownloadFileResponse getTemplateForCreateClient(
            @PathVariable TemplateTypeForPlantLoad templateType,
            @PathVariable long userId) {
            return reportService.getTemplateForCreateClient(templateType, userId);
    }

    @PostMapping("/load-measured-employees")
    public StandardResponse loadMeasuredEmployees() {
        return null;
    }

    @GetMapping("/get-employees-with-clothes-quantities/{userId}")
    public DownloadFileResponse getEmployeesWithClothesQuantities(
            @PathVariable long userId) {
        return reportService.getEmployeesWithClothesQuantities(userId);
    }
}
