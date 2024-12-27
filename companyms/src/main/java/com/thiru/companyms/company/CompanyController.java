package com.thiru.companyms.company;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {

	private CompanyService companyService;
	
	
	
	public CompanyController(CompanyService companyService)
	{
		this.companyService=companyService;
		
	}
	@GetMapping
	public List<Company> getAllCompanies()
	{
		return companyService.getAllCompanies();
	}
	@PutMapping("/{id}")
	public ResponseEntity<String> updateCompany(@PathVariable Long id,@RequestBody Company company)
	{
		companyService.updateCompany(company, id);
		System.out.print("Hello");
		return new ResponseEntity<>("Company updated successfully",HttpStatus.OK);
	}
	@PostMapping
	public ResponseEntity<String> addCompany(@RequestBody Company company)
	{
		companyService.createCompany(company);
		return new ResponseEntity<>("Company added successfully",HttpStatus.CREATED);
		
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteCompany(@PathVariable Long id)
	{
		boolean isDeleted = companyService.deleteCompanyById(id);
		if(isDeleted)
		{
			return new ResponseEntity<>("Company Deleted Successfully",HttpStatus.OK);
			
		}
		else
		{
			return new ResponseEntity<>("Company not found",HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/{id}")
	public  ResponseEntity<Company> getCompany(@PathVariable Long id)
	{
		Company company=companyService.getCompanyById(id);
		if(company!=null)
		{
			return new  ResponseEntity<>(company,HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
