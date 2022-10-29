package pl.bratosz.smartlockers.service.exels.plant.template.data;

import java.util.Objects;

public class TemplatePositionWithDepartment {
    private String positionName;
    private String departmentName;

    public TemplatePositionWithDepartment(String positionName, String departmentName) {
        this.positionName = positionName;
        this.departmentName = departmentName;
    }

    public TemplatePositionWithDepartment(String positionName) {
        this.positionName = positionName;
    }

    public String getPositionName() {
        return positionName;
    }

    public String getDepartmentName() {
        return departmentName;
    }
}
