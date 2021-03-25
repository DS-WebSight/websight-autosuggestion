import React from 'react';

import { optionData } from '../utils/AuthorizableAutosuggestionUtil.js';

const SelectTagIcon = (props) => {
    const iconStyle = {
        fontSize: '10px',
        verticalAlign: 'middle',
        paddingRight: '3px'
    };

    return <i className='material-icons' style={iconStyle} {...props}>{props.children}</i>;
};

const AUTHORIZABLE_TYPES = ['user', 'group', 'system_user']

const typeToLabel = {
    user: 'Users',
    group: 'Groups',
    system_user: 'System Users'
}

const groupOptionsByType = (options) => {
    return AUTHORIZABLE_TYPES.map((type) => ({
        label: typeToLabel[type] || type,
        options: options
            .filter((option) => (option.data.type === type))
    }));
}

class AuthorizableProcessorService {
    processMembersOptions(options) {
        return groupOptionsByType(options)
            .map((type) =>
                ({
                    ...type,
                    options: type.options.map((item) => optionData(item))
                })
            );
    }

    processGroupOption(option) {
        return optionData(option);
    }

    formatOptionLabel(option) {
        return (
            <>
                <SelectTagIcon className='material-icons'>{option.icon}</SelectTagIcon>
                {option.displayName}
            </>
        );
    }
}

export default new AuthorizableProcessorService();