
import React from 'react';
import styled from 'styled-components';

import RestClient from 'websight-rest-esm-client/RestClient.js';

const MORE_RESULTS_TEXT = 'More results available. Extend query.';
const REQUEST_DEBOUNCE_TIMEOUT_IN_MS = 300;

const MoreResultsContainer = styled.div`
    font-size: 12px;
    opacity: 0.7;
    height: 16px;
    display: flex;
    justify-content: center;
    align-content: center;
`;

const optionPostProcessor = (suggestions, customPostProcessors) => {
    const defaultPostProcessor = (item) => ({
        ...item,
        data: item.data || {},
        label: item.label || item.value
    });

    const moreResultsPostProcessor = (item) => ({
        ...item,
        notClickable: true,
        label: (
            <MoreResultsContainer>
                {item.value}
            </MoreResultsContainer>
        )
    });

    const hasChildren = (mappedSuggestion) => {
        if (mappedSuggestion.data && mappedSuggestion.data.hasChildren !== undefined) {
            return mappedSuggestion.data.hasChildren
        } else {
            return true;
        }
    }

    if (customPostProcessors.optionsPostProcessor) {
        return customPostProcessors.optionsPostProcessor(suggestions);
    } else {
        return suggestions
            .map(suggestion => {
                let mappedSuggestion;

                if (suggestion.moreResultsOption) {
                    mappedSuggestion = moreResultsPostProcessor(suggestion);
                } else if (customPostProcessors.optionPostProcessor) {
                    mappedSuggestion = customPostProcessors.optionPostProcessor(suggestion);
                } else {
                    mappedSuggestion = defaultPostProcessor(suggestion);
                }

                return {
                    hasChildren: hasChildren(mappedSuggestion),
                    ...mappedSuggestion
                }
            });
    }
}

class AutosuggestionService {
    constructor() {
        this.restClient = new RestClient('websight-autosuggestion-service');

        this.getOptions = this.getOptions.bind(this);
        this.debounce = this.debounce.bind(this);
    }

    debounce(callback, timerId, wait) {
        clearTimeout(timerId);
        return setTimeout(() => callback.apply(this), wait ? wait : REQUEST_DEBOUNCE_TIMEOUT_IN_MS);
    }

    getMaxHeightOfDropDown(inputRef) {
        const defaultHeight = 500;
        const offset = 40;

        if (!inputRef) return defaultHeight;


        const elementInput = inputRef;
        const modalParent = elementInput.closest('div[role="dialog"]');
        const elementSpaceToBottom = window.innerHeight - elementInput.getBoundingClientRect().bottom;

        if (modalParent) {
            const spaceFromModalToBottom = window.innerHeight - modalParent.getBoundingClientRect().bottom;

            return elementSpaceToBottom - spaceFromModalToBottom - offset;
        } else {
            return defaultHeight < elementSpaceToBottom ? defaultHeight : elementSpaceToBottom - offset;
        }
    }

    getOptions(parameters, customPostProcessors, onSuccessHandler, filterOut) {
        return this.restClient.get({
            action: 'get-autosuggestion',
            parameters: parameters,
            onSuccess: (data) => {
                const { hasMoreResults } = data.entity;
                let { suggestions } = data.entity;

                if (filterOut) {
                    suggestions = suggestions
                        .filter(suggestion => !filterOut.includes(suggestion.value));
                }

                if (hasMoreResults) {
                    suggestions.push({ value: MORE_RESULTS_TEXT, isDisabled: true, moreResultsOption: true, data: {} });
                }
                const options = optionPostProcessor(suggestions, customPostProcessors);
                onSuccessHandler(options)
            },
            onFailure: (data) => console.warn(data),
            onNonFrameworkError: (error) => console.warn(error)
        });
    }
}

export const { debounce, getMaxHeightOfDropDown, getOptions } = new AutosuggestionService();