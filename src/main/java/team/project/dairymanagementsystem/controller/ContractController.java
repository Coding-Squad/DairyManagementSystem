package team.project.dairymanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.project.dairymanagementsystem.model.Contract;
import team.project.dairymanagementsystem.model.Supplier;
import team.project.dairymanagementsystem.model.enumerated.Status;
import team.project.dairymanagementsystem.service.ContractService;
import team.project.dairymanagementsystem.service.SupplierService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/contract")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @Autowired
    private SupplierService supplierService;


    @GetMapping("/contract")
    public String addContract(Model model){
        model.addAttribute("supplier",new Supplier());
        return "ContractForm";
    }

    @GetMapping("/supplier/{national_id}")
    public String supplierDetails(@PathVariable(name = "national_id") Integer national_id){
        String UPLOADED_FOLDER = "C:\\Users\\enrico\\Desktop\\";
        byte[] pic = supplierService.getSupplier(national_id).getPic();

        try{
            Path path = Paths.get(UPLOADED_FOLDER+"try.pdf");
            Files.write(path, pic);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @PostMapping("/newcontract")
    public String addContract(@ModelAttribute(name = "supplier") Supplier supplier, MultipartFile file){
        supplier.getContract().setStatus(Status.PENDING.toString());
        supplier.getContract().setSupplierId(supplier.getNationalId());
        System.out.println(supplier.getNationalId());
        try{
            //get uploaded files in bytes
            byte[] bytes = file.getBytes();
            //set bytes to corresponding supplier attributes
            supplier.setPic(bytes);
        }catch (Exception e){
            e.printStackTrace();
        }

        this.supplierService.createSupplier(supplier);
        return "redirect:/";
    }

    @GetMapping("/contracts")
    public String getAllContracts(Model model){
        List<Contract> contracts = this.contractService.getAllContracts();
        List<Supplier> suppliers = this.supplierService.getAllSuppliers();
        model.addAttribute("contracts", contracts);
        model.addAttribute("suppliers", suppliers);
        return "contracts";
    }
    @PostMapping("/approve/{id}")
    public String approveContract(@PathVariable(name = "id") int id) {
        String msg = this.contractService.approveContract(id);
        return "redirect:/contract/contracts";
    }

    @PostMapping("/deny/{id}")
    public String denyContract(@PathVariable(name = "id") int id) {
        String msg = this.contractService.denyContract(id);
        return "redirect:/contract/contracts";
    }

    @PostMapping("/cancel/{id}")
    public String cancelContract(@PathVariable(name = "id") int id) {
        String msg = this.contractService.cancelContract(id);
        return "redirect:/contract/contracts";
    }

    @PostMapping("/delete/{id}")
    public String deleteContract(@PathVariable(name = "id") int id) {
        String msg = this.contractService.deleteContract(id);
        return "redirect:/contract/contracts";
    }

    @GetMapping("/view-supplier/{id}")
    public String viewSupplier(@PathVariable(name = "id") int id, Model model) {
        Supplier supplier = this.supplierService.getSupplier(id);
        model.addAttribute("supplier", supplier);
        return "viewSupplier";
    }

}
