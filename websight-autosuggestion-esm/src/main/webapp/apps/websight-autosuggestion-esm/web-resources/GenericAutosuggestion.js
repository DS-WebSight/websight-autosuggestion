import React from 'react';
import { AsyncSelect, components } from '@atlaskit/select';
import { layers } from '@atlaskit/theme';

import RestClient from 'websight-rest-esm-client/RestClient.js';

import { debounce, getMaxHeightOfDropDown, getOptions } from './services/AutosuggestionService.js';

/**
 * GenericAutosuggestion component allows to find a suggested options based on a query.
 *      It can be configureed to support selection of multiple options.
 *      Each option is a single object that can be choosen.
 */
export default class GenericAutosuggestion extends React.Component {

    /**
     * @param props = {
     *      placeholder: a short hint that describes the expected value of an input field,
     *      parameters: {
     *          autosuggestionType: e.g. authorizable etc.,
     *          ...otherParams
     *      },
     *      optionPostProcessor: post processor to format simple autosuggestion, e.g. add icons,
     *      optionsPostProcessor: post processor to format all autosuggestions
     *      defaultValue: define a default value of autosuggester
     *      formatOptionLabel: define a function that takes an option and returns it's appearance on list
     *      isMulti: define if input should be multiple choice,
     *      label: label to be displayed in the heading component,
     *      name: name of the HTML Input (optional - without this, no input will be rendered),
     *      noOptionsMessage: function that takes input value and returns message to show when there is no options,
     *      noOptionEmptyMessage: message to show when input is empty, e.g. Start typing to find
     *      onChange: function that is triggered on options change
     *      spacing: AsyncSelect appearance type
     *      value: define a value of autosuggester
     *      filterOut: define an array of filters
     * }
     */
    constructor(props) {
        super(props);

        this.state = {
            maxHeight: 500
        }

        this.restClient = new RestClient('websight-autosuggestion-service');
    }

    componentDidMount() {
        this.setState({ maxHeight: getMaxHeightOfDropDown(this.inputRef) });
    }

    loadOptions(query, callback) {
        const { parameters, optionPostProcessor, optionsPostProcessor, filterOut } = this.props;
        const customPostProcessors = { optionPostProcessor, optionsPostProcessor };

        getOptions(
            { ...parameters, query },
            customPostProcessors,
            (options) => {
                callback(options);
            },
            filterOut
        );
    }

    render() {
        const { maxHeight } = this.state;

        return (
            <AsyncSelect
                components={{
                    Input: (props) => {
                        const innerRef = (inputRef) => {
                            this.inputRef = inputRef;
                            props.innerRef(inputRef);
                        }
                        return <components.Input {...props} innerRef={innerRef} />;
                    }
                }}
                defaultValue={this.props.defaultValue}
                formatOptionLabel={this.props.formatOptionLabel}
                isMulti={this.props.isMulti}
                isClearable={true}
                label={this.props.label}
                loadOptions={(...args) =>
                    this.debounceTimerId = debounce(() => this.loadOptions(...args), this.debounceTimerId)
                }
                maxMenuHeight={maxHeight}
                menuPortalTarget={document.body}
                name={this.props.name}
                noOptionsMessage={({ inputValue }) =>
                    inputValue ?
                        (this.props.noOptionsMessage && this.props.noOptionsMessage(inputValue))
                        :
                        this.props.noOptionEmptyMessage}
                onChange={this.props.onChange}
                placeholder={this.props.placeholder}
                ref={(element) => this.select = element}
                spacing={this.props.spacing}
                styles={{
                    menuPortal: base => ({
                        ...base,
                        zIndex: layers.tooltip()
                    })
                }}
                value={this.props.value}
            />)
    }
}
