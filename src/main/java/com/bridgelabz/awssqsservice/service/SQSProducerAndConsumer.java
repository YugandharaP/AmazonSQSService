package com.bridgelabz.awssqsservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageRequest;

@Service
public class SQSProducerAndConsumer implements ISQSService {
	AWSCredentials credentials = new BasicAWSCredentials("****","****");

	AmazonSQS sqs = AmazonSQSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withRegion(Regions.US_EAST_1).build();

	String fifoQueueUrl;
	String standardQueueUrl;

	@Override
	public void createQueue() {
		CreateQueueRequest createStandardQueueRequest = new CreateQueueRequest("demo-queue");
		standardQueueUrl = sqs.createQueue(createStandardQueueRequest).getQueueUrl();
	}

	@Override
	public void createFifoQueue() {

		Map<String, String> queueAttributes = new HashMap<>();
		queueAttributes.put("FifoQueue", "true");
		queueAttributes.put("ContentBasedDeduplication", "true");
		CreateQueueRequest createFifoQueueRequest = new CreateQueueRequest("fifo-queue.fifo")
				.withAttributes(queueAttributes);
		fifoQueueUrl = sqs.createQueue(createFifoQueueRequest).getQueueUrl();
	}

	@Override
	public void producer() {
		createFifoQueue();
		// Map for the Attributes which will hold the meta-data for our message
		Map<String, MessageAttributeValue> messageAttributes = new HashMap<String, MessageAttributeValue>();

		messageAttributes.put("AttributeOne",
				new MessageAttributeValue().withStringValue("Hii,Yuga is here").withDataType("String"));

		/*
		 * SendMessageRequest sendMessageStandardQueue = new SendMessageRequest()
		 * .withQueueUrl("https://sqs.us-east-1.amazonaws.com/961621708136/sqs-demo")
		 * .withMessageBody("A simple message.").withDelaySeconds(30).
		 * withMessageAttributes(messageAttributes);
		 * 
		 * sqs.sendMessage(sendMessageStandardQueue);
		 */

		SendMessageRequest sendMessageFifoQueue = new SendMessageRequest().withQueueUrl(fifoQueueUrl)
				.withMessageBody("Ganesh Chaturthi comming").withMessageGroupId("demo-group-1")
				.withMessageAttributes(messageAttributes);
		sqs.sendMessage(sendMessageFifoQueue);
	}

	@Override
	public List<Message> consumer() {
		createFifoQueue();
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(fifoQueueUrl).withWaitTimeSeconds(10)
				.withMaxNumberOfMessages(8).withWaitTimeSeconds(10);

		List<Message> sqsMessages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		return sqsMessages;
	}

	@Override
	public int sendMultipleMessages() {
		createFifoQueue();
		int count = 0;
		List<SendMessageBatchRequestEntry> messageEntries = new ArrayList<>();
		messageEntries.add(new SendMessageBatchRequestEntry().withId("id-1").withMessageBody("bijayaLaksmi Senapati")
				.withMessageGroupId("demo-group-1"));
		count++;
		messageEntries.add(new SendMessageBatchRequestEntry().withId("id-2").withMessageBody("Saurav Manchanda")
				.withMessageGroupId("demo-group-1"));
		count++;
		SendMessageBatchRequest sendMessageBatchRequest = new SendMessageBatchRequest(fifoQueueUrl, messageEntries);
		sqs.sendMessageBatch(sendMessageBatchRequest);
		return count;
	}

	@Override
	public void deleteMessageFromQueue() {
		List<Message>sqsMessages=consumer();
		sqs.deleteMessage(new DeleteMessageRequest().withQueueUrl(fifoQueueUrl)
				.withReceiptHandle(sqsMessages.get(0).getReceiptHandle()));
	}

}
