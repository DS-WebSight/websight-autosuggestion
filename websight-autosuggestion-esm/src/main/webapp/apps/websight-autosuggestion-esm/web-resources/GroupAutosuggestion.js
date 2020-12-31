import React from 'react';

import GenericAutosuggestion from './GenericAutosuggestion.js';
import AuthorizableProcessorService from './services/AuthorizableProcessorService.js';

export default function GroupAutosuggestion(props) {
    return (
        <GenericAutosuggestion
            noOptionEmptyMessage='Start typing to find a group'
            noOptionsMessage={(inputValue) => `No groups found for "${inputValue}"`}
            {...props}
            formatOptionLabel={AuthorizableProcessorService.formatOptionLabel}
            optionPostProcessor={AuthorizableProcessorService.processGroupOption}
            parameters={{ autosuggestionType: 'group' }}
        />
    );
}