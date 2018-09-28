package com.bridgelabz.awssqsservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.sqs.model.Message;
import com.bridgelabz.awssqsservice.service.ISQSService;

@RestController
public class SQSController {
	@Autowired
	private ISQSService sqsService;
	
	
	
	@PostMapping("/createqueue")
	public String createQueue()
	{
		sqsService.createQueue();
		return "succussful done work of create queue";
	}
	
	
	@PostMapping("/producer")
	public String producer() {
		
		sqsService.producer();
		return "succussful done work of producer";
		
	}
	
	@PostMapping("/consumer")
	public List<Message> consumer() {
		
		List<Message> messageList = sqsService.consumer();
		return messageList;
		
	}
	
	@PostMapping("/createfifoqueue")
	public String createFIFOQueue() {
		sqsService.createFifoQueue();
		return "succussful done work create FIFO queue";
	}

	@PostMapping("/sendmultiplemessages")
	public int sendMultipleMessages() {
		int count = sqsService.sendMultipleMessages();
		return count;
	}
	
	@PostMapping("/deletemessage")
	public String deleteMessageFromQueue() {
		sqsService.deleteMessageFromQueue();
		return "successfully deleted message";
	}
}
