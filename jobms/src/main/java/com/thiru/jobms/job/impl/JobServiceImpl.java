package com.thiru.jobms.job.impl;


import com.thiru.jobms.job.Job;
import com.thiru.jobms.job.JobRepository;
import com.thiru.jobms.job.JobService;
import com.thiru.jobms.job.clients.CompanyClient;
import com.thiru.jobms.job.clients.ReviewClient;
import com.thiru.jobms.job.dto.JobDTO;
import com.thiru.jobms.job.external.Company;
import com.thiru.jobms.job.external.Review;
import com.thiru.jobms.job.mapper.JobMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

	private Long nextId=1L;
	JobRepository jobRepository;

	int attempt=0;

	@Autowired
	RestTemplate restTemplate;

    @Autowired
    private CompanyClient companyClient;
	private ReviewClient reviewClient;

	public JobServiceImpl(JobRepository jobRepository, CompanyClient companyClient
	, ReviewClient reviewClient)
	{
		this.jobRepository=jobRepository;
		this.companyClient=companyClient;
		this.reviewClient=reviewClient;
	}

	@Override
//	@CircuitBreaker(name = "companyBreaker" , fallbackMethod =
//	"companyBreakerFallback")
//	@Retry(name = "companyBreaker" , fallbackMethod =
//	"companyBreakerFallback")
//	@RateLimiter(name = "companyBreaker" ,
//			fallbackMethod = "companyBreakerFallback")
	public List<JobDTO> findAll() {
		System.out.println("Attempt: "+ ++attempt);
		List<Job> jobs=jobRepository.findAll();
		//List<JobWithCompanyDTO> jobWithCompanyDTOs=new ArrayList<>();

		return jobs.stream().map(this::converttoDto)
				.collect(Collectors.toList());
	}
	public List<String> companyBreakerFallback(Exception e)
	{
		List<String> list=new ArrayList<>();
		list.add("Dummy");
		return list;
	}
	private JobDTO converttoDto(Job job)
	{

		Company company=companyClient.getCompany(job.getId());
		List<Review> reviews=reviewClient.getReviews(job.getCompanyId());
		JobDTO jobDTO = JobMapper.mapToJobWithCompanyDto(job,company,reviews);
		return jobDTO;
	}
	@Override
	public void createJob(Job job) {
		job.setId(nextId++);
		jobRepository.save(job);
	}

	@Override
	public JobDTO getJobById(Long id) {

		Job job= jobRepository.findById(id).orElse(null);
		return converttoDto(job);
	}

	@Override
	public boolean deleteJobById(Long id) {
		try {
		jobRepository.deleteById(id);
		return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	@Override
	public boolean updateJob(Long id, Job updatedJob) {

		Optional <Job> jobOptional=jobRepository.findById(id);
			if(jobOptional.isPresent())
			{
				Job job=jobOptional.get();
				job.setTitle(updatedJob.getTitle());
				job.setDescription(updatedJob.getDescription());
				job.setMaxSalary(updatedJob.getMaxSalary());
				job.setMinSalary(updatedJob.getMinSalary());
				job.setLocation(updatedJob.getLocation());
				jobRepository.save(job);
				return true;
			}
		return false;
	}



}
