package org.streampipes.processors.textmining.flink.processor.wordcount;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.textmining.flink.config.TextMiningFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class WordCountController extends FlinkDataProcessorDeclarer<WordCountParameters> {

	private static final String RESOURCE_ID = "strings.wordcount";
	private static final String PE_ID = "org.streampipes.processors.textmining.flink.wordcount";

	private static final String WORD_COUNT_FIELD_KEY = "wordcountField";
	private static final String TIME_WINDOW_KEY = "timeWindowKey";
	private static final String WORD_KEY = "word";
	private static final String COUNT_KEY = "count";

	@Override
	public DataProcessorDescription declareModel() {
		return ProcessingElementBuilder.create(getLabel(PE_ID))
						.category(DataProcessorType.AGGREGATE)
						.requiredStream(StreamRequirementsBuilder
										.create()
										.requiredPropertyWithUnaryMapping(
														EpRequirements.stringReq(),
														getLabel(WORD_COUNT_FIELD_KEY),
														PropertyScope.NONE)
										.build())
						.outputStrategy(OutputStrategies.fixed(EpProperties.stringEp(
										getLabel(WORD_KEY),
										"word",
										"http://schema.org/text"), EpProperties.integerEp(getLabel(COUNT_KEY), "count", "http://schema.org/number")))
						.requiredIntegerParameter(getLabel(TIME_WINDOW_KEY))
						.build();
	}

	@Override
	public FlinkDataProcessorRuntime<WordCountParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

		String fieldName = extractor.mappingPropertyValue(WORD_COUNT_FIELD_KEY);
		Integer timeWindowValue = extractor.singleValueParameter(TIME_WINDOW_KEY, Integer.class);

		return new WordCountProgram(new WordCountParameters(graph, fieldName, timeWindowValue), TextMiningFlinkConfig.INSTANCE.getDebug());

	}

	private Label getLabel(String id) {
		return Labels.fromResources(RESOURCE_ID, id);
	}
}