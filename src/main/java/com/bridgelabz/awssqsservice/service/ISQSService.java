package com.bridgelabz.awssqsservice.service;

import java.util.List;

import com.amazonaws.services.sqs.model.Message;

public interface ISQSService {

	public void producer();
	public List<Message> consumer();
	public void createQueue();
	public void createFifoQueue();
	int sendMultipleMessages();
	public void deleteMessageFromQueue();
}
