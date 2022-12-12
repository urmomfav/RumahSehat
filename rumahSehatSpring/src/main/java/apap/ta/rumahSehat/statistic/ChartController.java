package apap.ta.rumahSehat.statistic;

import apap.ta.rumahSehat.appointment.model.AppointmentModel;
import apap.ta.rumahSehat.appointment.service.AppointmentService;
import apap.ta.rumahSehat.statistic.dto.BarChartRequestDTO;
import apap.ta.rumahSehat.statistic.dto.DailyLineChartRequestDTO;
import apap.ta.rumahSehat.statistic.dto.MonthlyLineChartRequestDTO;
import apap.ta.rumahSehat.user.model.DokterModel;
import apap.ta.rumahSehat.user.service.DokterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/chart")
public class ChartController {
    @Autowired
    AppointmentService appointmentService;

    @Autowired
    ChartService chartService;

    @Autowired
    DokterService dokterService;

    @RequestMapping(value = "")
    private String defaultPage(Model model) {
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime awal = LocalDateTime.of(date.getYear(), 1, 1, 0, 0);
        LocalDateTime akhir = awal.plusYears(1).minusMinutes(1);

        List<AppointmentModel> appointmentModelList = appointmentService.getAppointmentInRange(awal, akhir);
        List<String> monthList = new ArrayList<>();

        for (int i=0; i<12 ; i++) monthList.add(awal.plusMonths(i).getMonth().toString() + " " + date.getYear());

        model.addAttribute("data", chartService.getDataPendapatanDokter(appointmentModelList));
        model.addAttribute("tahun", date.getYear());
        model.addAttribute("label", monthList);

        return"statistic/default";
    }

    @RequestMapping(value = "/bar")
    private String formBarChart(Model model) {
        BarChartRequestDTO barChartRequestDTO = new BarChartRequestDTO();

        barChartRequestDTO.setDokterModelList(new ArrayList<>());
        barChartRequestDTO.getDokterModelList().add(new DokterModel());

        List<DokterModel> dokterModelList = dokterService.findAll();

        model.addAttribute("barChartRequest", barChartRequestDTO);
        model.addAttribute("listDokter", dokterModelList);

        return "statistic/form-bar-chart";
    }

    @PostMapping(value = "/bar", params = "addRowDokter")
    private String formBarChartAddRowDokter(@ModelAttribute BarChartRequestDTO barChartRequestDTO,
                                            Model model) {
        if (barChartRequestDTO.getDokterModelList() == null) {
            barChartRequestDTO.setDokterModelList(new ArrayList<>());
        }

        List<DokterModel> dokterModelList = dokterService.findAll();

        if (barChartRequestDTO.getDokterModelList().size() == 8) {
            model.addAttribute("error", "Hanya dapat menampilkan bar chart 8 dokter");
            model.addAttribute("listDokter", dokterModelList);
            model.addAttribute("barChartRequest", barChartRequestDTO);

            return "statistic/form-bar-chart";
        }

        barChartRequestDTO.getDokterModelList().add(new DokterModel());

        model.addAttribute("barChartRequest", barChartRequestDTO);
        model.addAttribute("listDokter", dokterModelList);

        return "statistic/form-bar-chart";
    }

    @PostMapping(value = "/bar", params = "deleteRowDokter")
    private String formBarChartDeleteRowDokter(@ModelAttribute BarChartRequestDTO barChartRequestDTO,
                                               @RequestParam("deleteRowDokter") Integer row,
                                               Model model) {
        if (barChartRequestDTO.getDokterModelList() == null) {
            barChartRequestDTO.setDokterModelList(new ArrayList<>());
        }

        if (barChartRequestDTO.getDokterModelList().size() > 1) {
            barChartRequestDTO.getDokterModelList().remove(row.intValue());
        }

        List<DokterModel> dokterModelList = dokterService.findAll();

        model.addAttribute("barChartRequest", barChartRequestDTO);
        model.addAttribute("listDokter", dokterModelList);

        return "statistic/form-bar-chart";
    }

    @PostMapping(value = "/bar", params = "save")
    private String formBarChartSubmit(@ModelAttribute BarChartRequestDTO barChartRequestDTO,
                                      Model model) {
        String[] label = new String[barChartRequestDTO.getDokterModelList().size()];

        for (int i=0; i<label.length; i++) {
            DokterModel dokterModel = dokterService.findDokterByUsername(barChartRequestDTO.getDokterModelList().get(i).getUsername());
            label[i] = dokterModel.getNama();
        }

        int[] data = chartService.getBarChartData(barChartRequestDTO);

        model.addAttribute("label", label);
        model.addAttribute("data", data);
        model.addAttribute("tipe", barChartRequestDTO.getTipe());

        return "statistic/bar-chart";
    }

    @GetMapping(value = "/line-monthly")
    private String formLineChartMonthly(Model model) {
        MonthlyLineChartRequestDTO monthlyLineChartRequestDTO = new MonthlyLineChartRequestDTO();
        monthlyLineChartRequestDTO.setDokterModelList(new ArrayList<>());
        monthlyLineChartRequestDTO.getDokterModelList().add(new DokterModel());

        List<DokterModel> dokterModelList = dokterService.findAll();

        model.addAttribute("lineChartRequest", monthlyLineChartRequestDTO);
        model.addAttribute("listDokter", dokterModelList);

        return "statistic/form-line-chart-monthly";
    }

    @PostMapping(value = "line-monthly", params = "addRowDokter")
    private String formLineChartMonthlyAddRowDokter(@ModelAttribute MonthlyLineChartRequestDTO monthlyLineChartRequestDTO,
                                                    Model model) {
        if (monthlyLineChartRequestDTO.getDokterModelList() == null) {
            monthlyLineChartRequestDTO.setDokterModelList(new ArrayList<>());
        }

        List<DokterModel> dokterModelList = dokterService.findAll();

        if (monthlyLineChartRequestDTO.getDokterModelList().size() == 5) {
            model.addAttribute("error", "Hanya dapat menampilkan line chart 5 dokter");
            model.addAttribute("listDokter", dokterModelList);
            model.addAttribute("lineChartRequest", monthlyLineChartRequestDTO);

            return "statistic/form-line-chart-monthly";
        }

        monthlyLineChartRequestDTO.getDokterModelList().add(new DokterModel());

        model.addAttribute("lineChartRequest", monthlyLineChartRequestDTO);
        model.addAttribute("listDokter", dokterModelList);

        return "statistic/form-line-chart-monthly";
    }

    @PostMapping(value = "/line-monthly", params = "deleteRowDokter")
    private String formLineChartMonthlyDeleteRowDokter(@ModelAttribute MonthlyLineChartRequestDTO monthlyLineChartRequestDTO,
                                                       @RequestParam("deleteRowDokter") Integer row,
                                                       Model model) {
        if (monthlyLineChartRequestDTO.getDokterModelList() == null) {
            monthlyLineChartRequestDTO.setDokterModelList(new ArrayList<>());
        }

        if (monthlyLineChartRequestDTO.getDokterModelList().size() > 1) {
            monthlyLineChartRequestDTO.getDokterModelList().remove(row.intValue());
        }

        List<DokterModel> dokterModelList = dokterService.findAll();

        model.addAttribute("lineChartRequest", monthlyLineChartRequestDTO);
        model.addAttribute("listDokter", dokterModelList);

        return "statistic/form-line-chart-monthly";
    }

    @PostMapping(value = "/line-monthly", params = "save")
    private String formLineChartMonthlySubmit(@ModelAttribute MonthlyLineChartRequestDTO monthlyLineChartRequestDTO,
                                              Model model) {

        String[] listDokter = new String[monthlyLineChartRequestDTO.getDokterModelList().size()];

        for (int i=0; i<listDokter.length; i++) {
            DokterModel dokterModel = dokterService.findDokterByUsername(monthlyLineChartRequestDTO.getDokterModelList().get(i).getUsername());
            listDokter[i] = dokterModel.getNama();
        }

        LocalDateTime awal = LocalDateTime.of(monthlyLineChartRequestDTO.getTahun(), 1, 1, 0, 0);

        List<String> monthList = new ArrayList<>();

        for (int i=0; i<12 ; i++) monthList.add(awal.plusMonths(i).getMonth().toString() + " " + monthlyLineChartRequestDTO.getTahun());

        model.addAttribute("tahun", monthlyLineChartRequestDTO.getTahun());
        model.addAttribute("listDokter", listDokter);
        model.addAttribute("data", chartService.getDataLineChartMonthly(
                monthlyLineChartRequestDTO.getDokterModelList(),
                monthlyLineChartRequestDTO.getTahun()
        ));
        model.addAttribute("label", monthList);

        return "statistic/line-chart";
    }

    @GetMapping(value = "/line-daily")
    private String formLineChartDaily(Model model) {
        DailyLineChartRequestDTO dailyLineChartRequestDTO = new DailyLineChartRequestDTO();
        dailyLineChartRequestDTO.setDokterModelList(new ArrayList<>());
        dailyLineChartRequestDTO.getDokterModelList().add(new DokterModel());

        List<DokterModel> dokterModelList = dokterService.findAll();

        model.addAttribute("lineChartRequest", dailyLineChartRequestDTO);
        model.addAttribute("listDokter", dokterModelList);

        return"statistic/form-line-chart-daily";
    }

    @PostMapping(value = "line-daily", params = "addRowDokter")
    private String formLineChartDailyAddRowDokter(@ModelAttribute DailyLineChartRequestDTO dailyLineChartRequestDTO,
                                                  Model model) {
        if (dailyLineChartRequestDTO.getDokterModelList() == null) {
            dailyLineChartRequestDTO.setDokterModelList(new ArrayList<>());
        }

        List<DokterModel> dokterModelList = dokterService.findAll();

        if (dailyLineChartRequestDTO.getDokterModelList().size() == 5) {
            model.addAttribute("error", "Hanya dapat menampilkan line chart 5 dokter");
            model.addAttribute("listDokter", dokterModelList);
            model.addAttribute("lineChartRequest", dailyLineChartRequestDTO);

            return "statistic/form-line-chart-daily";
        }

        dailyLineChartRequestDTO.getDokterModelList().add(new DokterModel());

        model.addAttribute("lineChartRequest", dailyLineChartRequestDTO);
        model.addAttribute("listDokter", dokterModelList);

        return "statistic/form-line-chart-daily";
    }

    @PostMapping(value = "/line-daily", params = "deleteRowDokter")
    private String formLineChartDailyDeleteRowDokter(@ModelAttribute DailyLineChartRequestDTO dailyLineChartRequestDTO,
                                                       @RequestParam("deleteRowDokter") Integer row,
                                                       Model model) {
        if (dailyLineChartRequestDTO.getDokterModelList() == null) {
            dailyLineChartRequestDTO.setDokterModelList(new ArrayList<>());
        }

        if (dailyLineChartRequestDTO.getDokterModelList().size() > 1) {
            dailyLineChartRequestDTO.getDokterModelList().remove(row.intValue());
        }

        List<DokterModel> dokterModelList = dokterService.findAll();

        model.addAttribute("lineChartRequest", dailyLineChartRequestDTO);
        model.addAttribute("listDokter", dokterModelList);

        return "statistic/form-line-chart-daily";
    }

    @PostMapping(value = "/line-daily", params = "save")
    private String formLineChartDailySubmit(@ModelAttribute DailyLineChartRequestDTO dailyLineChartRequestDTO,
                                            Model model) {

        String[] listDokter = new String[dailyLineChartRequestDTO.getDokterModelList().size()];
        List<DokterModel> listDokterDb = new ArrayList<>();

        for (int i=0; i<listDokter.length; i++) {
            DokterModel dokterModel = dokterService.findDokterByUsername(dailyLineChartRequestDTO.getDokterModelList().get(i).getUsername());
            listDokter[i] = dokterModel.getNama();
            listDokterDb.add(dokterModel);
        }

        LocalDateTime awal = LocalDateTime.of(
                dailyLineChartRequestDTO.getBulanTahun().getYear(),
                dailyLineChartRequestDTO.getBulanTahun().getMonthValue(),
                1,
                0,
                0);

        int dayNumber = awal.plusMonths(1).minusMinutes(1).getDayOfMonth();
        int[] dayList = new int[dayNumber];

        for (int i=0; i<dayNumber; i++) dayList[i] = i+1;

        model.addAttribute("tahun", awal.getMonth().toString() + " " + awal.getYear());
        model.addAttribute("listDokter", listDokter);
        model.addAttribute("data", chartService.getDataLineChartDaily(
                listDokterDb,
                awal.getYear(),
                awal.getMonthValue()
        ));
        model.addAttribute("label", dayList);

        return "statistic/line-chart";
    }
}
