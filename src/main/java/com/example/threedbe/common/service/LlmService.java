package com.example.threedbe.common.service;

import static org.springframework.ai.openai.api.OpenAiApi.ChatModel.*;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.stereotype.Service;

import com.example.threedbe.common.dto.LlmResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LlmService {

	private final ChatClient chatClient;

	public LlmResponseDto generate(String content) {
		BeanOutputConverter<LlmResponseDto> outputConverter = new BeanOutputConverter<>(LlmResponseDto.class);
		Prompt prompt = createPrompt(content, outputConverter);
		String responseContent = executePrompt(prompt);

		return outputConverter.convert(responseContent);
	}

	private Prompt createPrompt(String content, BeanOutputConverter<LlmResponseDto> outputConverter) {
		String jsonSchema = outputConverter.getJsonSchema();
		OpenAiChatOptions options = OpenAiChatOptions.builder()
			.model(GPT_4_O_MINI)
			.responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
			.build();

		return new Prompt(content, options);
	}

	private String executePrompt(Prompt prompt) {
		ChatResponse chatResponse = chatClient.prompt(prompt)
			.call()
			.chatResponse();

		if (chatResponse == null) {
			throw new IllegalStateException("AI response is null");
		}

		return chatResponse
			.getResult()
			.getOutput()
			.getText();
	}

}
