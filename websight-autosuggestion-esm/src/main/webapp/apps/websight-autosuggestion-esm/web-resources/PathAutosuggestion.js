import React from 'react';
import TextField from '@atlaskit/textfield';
import Tooltip from '@atlaskit/tooltip';
import styled from 'styled-components';

import { colors } from '@atlaskit/theme';

import { debounce, getMaxHeightOfDropDown, getOptions } from './services/AutosuggestionService.js';
import { ClearButton } from './utils/Buttons.js';
import { LoadingWrapper } from './utils/Wrappers.js';

const OPTIONS_CONTAINER_CLASS = 'autosuggestion-options';
const SELECTED_OPTION_CLASS = 'autosuggestion-option--selected';
const OPTION_ROW_HEIGHT = 32;

const AutosuggestionContainer = styled.div`
    display: flex;
    width: 100%;
    position: relative;
`;

const AutosuggestionInputContainer = styled.div`
    cursor: pointer;
    width: 100%;

    & > div {
        border-width: 1px;
        height: 32px;
    }

    input {
        cursor: pointer;
    }

    input:focus ~ .autosuggestion-options--no-options {
        align-items: center;
        color: ${colors.N80};
        display: flex;
        justify-content: center;
    }
`;

const AutosuggestionOptionsContainer = styled.div`
    background: ${colors.N0};
    box-shadow: 0px 4px 8px 0px ${colors.N20};
    border: 1px solid ${colors.N30};
    border-radius: 5px;
    display: none;
    min-height: 30px;
    overflow-y: auto;
    padding: 10px 0;
    position: absolute;
    top: 39px;
    width: calc(100% - 4px);
    z-index: 10;

    &.autosuggestion-options--loading > div {
        cursor: auto;
        filter: grayscale(100%);
        opacity: 0.4;
    }
`;

const AutosuggestionOptionContainer = styled.div`
    padding: 6px 10px;
    cursor: pointer;

    &.autosuggestion-option--disabled {
        cursor: auto;
    }

    &:not(.autosuggestion-option--disabled):hover,
    &.autosuggestion-option--selected {
        background: ${colors.N20};
    }
`;

const ArrowDownIcon = styled.i`
    margin-right: 6px
    cursor: pointer;
`;

const ResetButtonContainer = styled.div`
    display: inline;
`;

const removeTrailingSlashFromPath = (path) => {
    return (path && path !== '/') ? path.replace(/\/$/, '') : path;
}

const ensureTrailingSlashInPath = (path) => {
    if (!path || path === '/') {
        return path;
    }
    const lastChar = path.slice(-1);
    return lastChar === '/' ? path : path + '/';
}

/**
 * PathAutosuggestion component is dedicated to path base suggestions. It allows to explore paths level by level.
 *      API endpoint needs to take at least two parameters: { query, basePath }, and response with the list
 *      of resources on a level defined by basePath.
 *      By addition, it supports Tab key that fills the query path with common part of suggested options.
 */
export default class PathAutosuggestion extends React.Component {

    /**
     * @param props = {
     *      placeholder: a short hint that describes the expected value of an input field,
     *      parameters: {
     *          autosuggestionType: e.g. path,
     *          ...otherParams
     *      },
     *      optionPostProcessor: post processor to format autosuggestion, e.g. add icons,
     *      noOptionsMessage: function that takes input value and returns message or string message to show when there is no options,
     *      onChange: function that is triggered on options change
     *      value: define a value of autosuggester
     *      styles: pass object of styles to extend default input styles
     * }
     */
    constructor(props) {
        super(props);

        this.state = {
            options: [],
            optionsOpen: false,
            searchInputValue: props.value || props.defaultValue || '/',
            previousSearchInputValue: '',
            maxHeight: 500,
            loading: true,
            selectedOptionIndex: -1,
            searchValueHasChildren: false
        }

        this.selectOption = this.selectOption.bind(this);
        this.onChange = this.onChange.bind(this);
        this.onKeyDown = this.onKeyDown.bind(this);
        this.onKeyUp = this.onKeyUp.bind(this);
        this.onClick = this.onClick.bind(this);
        this.openOptions = this.openOptions.bind(this);
        this.onBlur = this.onBlur.bind(this);
        this.scrollToResourceRow = this.scrollToResourceRow.bind(this);

        this.shiftPressed = false;
    }

    componentDidMount() {
        this.setState({
            maxHeight: getMaxHeightOfDropDown(this.inputRef)
        });
    }

    componentDidUpdate(prevProps) {
        const { value } = this.props;

        if (value && prevProps.value !== value) {
            this.setState({ searchInputValue: value })
        }
    }

    destructureSearchInputValue(optionValue) {
        const searchInputValueArray = this.state.searchInputValue
            .split('/');

        const query = searchInputValueArray.splice(-1, 1)[0];
        const basePath = `${searchInputValueArray.join('/')}`;
        const searchInputValue = [basePath, optionValue || query]
            .join('/');

        return { basePath, query, searchInputValue }
    }

    loadOptions() {
        const { parameters, optionPostProcessor } = this.props;
        const { query, basePath } = this.destructureSearchInputValue();

        getOptions(
            { ...parameters, query, basePath, returnRelativePath: true },
            { optionPostProcessor },
            (options) => {
                this.setState({ options, loading: false });
            }
        )
    }

    selectOption(index) {
        const { loading } = this.state;

        if (loading) return;

        this.setState(
            ({ options }) => {
                const option = options[index];

                if (option) {
                    const { searchInputValue } = this.destructureSearchInputValue(options[index].value);

                    !option.hasChildren && this.inputRef.blur();

                    return ({
                        searchInputValue: option.hasChildren ? `${searchInputValue}/` : searchInputValue,
                        selectedOptionIndex: -1,
                        loading: true,
                        searchValueHasChildren: option.hasChildren
                    });
                }
            },
            () => this.loadOptions()
        );
    }

    scrollToResourceRow() {
        const el = document.querySelector(`.${SELECTED_OPTION_CLASS}`);
        const container = document.querySelector(`.${OPTIONS_CONTAINER_CLASS}`);
        if (el) {
            const indexOfElement = [...el.parentElement.childNodes].indexOf(el);
            el && container.scrollTo(0, indexOfElement * OPTION_ROW_HEIGHT);
        }
    }

    onKeyDown(event) {
        const { onChange } = this.props;

        const moveOneOptionDown = () =>
            this.setState(({ selectedOptionIndex, options }) => {
                options = options.filter(({ notClickable }) => !notClickable);

                if (selectedOptionIndex < 0) {
                    return { selectedOptionIndex: 0 };
                } else if (selectedOptionIndex >= options.length - 1) {
                    return { selectedOptionIndex: -1 };
                } else {
                    return { selectedOptionIndex: selectedOptionIndex + 1 };
                }
            }, this.scrollToResourceRow);

        const moveOneOptionUp = () =>
            this.setState(({ selectedOptionIndex, options }) => {
                options = options.filter(({ notClickable }) => !notClickable);

                if (selectedOptionIndex < 0) {
                    return { selectedOptionIndex: options.length - 1 };
                } else if (selectedOptionIndex === 0) {
                    return { selectedOptionIndex: -1 };
                } else {
                    return { selectedOptionIndex: selectedOptionIndex - 1 };
                }
            }, this.scrollToResourceRow);

        switch(event.key){
        case 'Tab':
            if (this.state.optionsOpen) {
                event.stopPropagation();
                event.preventDefault();
                if (!this.shiftPressed) {
                    moveOneOptionDown();
                } else {
                    moveOneOptionUp();
                }
            }
            break;
        case 'ArrowUp':
            event.preventDefault();
            this.state.optionsOpen ? moveOneOptionUp() : this.openOptions();
            break;
        case 'ArrowDown':
            event.preventDefault();
            this.state.optionsOpen ? moveOneOptionDown() : this.openOptions();
            break;
        case 'Enter':
            if (this.state.optionsOpen) {
                event.stopPropagation();
                event.preventDefault();
                if (this.state.selectedOptionIndex >= 0) {
                    this.selectOption(this.state.selectedOptionIndex);
                } else {
                    let { searchInputValue } = this.destructureSearchInputValue();
                    searchInputValue = removeTrailingSlashFromPath(searchInputValue);
                    this.setState({
                        searchInputValue: searchInputValue,
                        optionsOpen: false
                    }, onChange(searchInputValue));
                }
            }
            break;
        case 'Escape':
            event.stopPropagation();
            event.preventDefault();
            this.setState(({ previousSearchInputValue }) =>
                ({ searchInputValue: previousSearchInputValue })
            );
            this.inputRef.blur();
            break;
        case 'Shift':
            this.shiftPressed = true;
            break
        default:
            break;
        }
    }

    onKeyUp(event) {
        switch(event.key){
        case 'Shift':
            this.shiftPressed = false;
            break
        default:
            break;
        }
    }

    onClick() {
        if (this.state.optionsOpen) {
            this.setState({ optionsOpen: false })
        } else {
            this.openOptions();
        }
    }

    onChange(event) {
        const value = event.target.value;

        const valueWithoutRedundandSlashes = value.replace(/\/\/+/g, '/');

        this.setState(
            { searchInputValue: valueWithoutRedundandSlashes || '/', loading: true, optionsOpen: true },
            () => {
                this.debounceTimerId = debounce(() => this.loadOptions(), this.debounceTimerId)
            })
    }

    openOptions() {
        this.setState(
            ({ searchValueHasChildren, searchInputValue }) => ({
                loading: true,
                optionsOpen: true,
                previousSearchInputValue: searchInputValue,
                searchInputValue: searchValueHasChildren ? ensureTrailingSlashInPath(searchInputValue) : searchInputValue,
                options: []
            }),
            () => this.loadOptions()
        )
    }

    onBlur() {
        const { onChange } = this.props;

        this.setState(
            ({ searchInputValue }) => ({
                optionsOpen: false,
                searchInputValue: removeTrailingSlashFromPath(searchInputValue)
            }),
            () => onChange(this.state.searchInputValue)
        )
    }

    render() {
        const { noOptionsMessage, styles,
            placeholder } = this.props;

        const optionsStyles = this.state.optionsOpen ? { display: 'block' } : undefined;

        return (
            <>
                <AutosuggestionContainer>
                    <AutosuggestionInputContainer>
                        <TextField
                            autocomplete='off'
                            value={this.state.searchInputValue}
                            placeholder={placeholder}
                            style={{ ...styles }}
                            onKeyUp={this.onKeyUp}
                            onKeyDown={this.onKeyDown}
                            onClick={this.onClick}
                            onChange={this.onChange}
                            onBlur={this.onBlur}
                            ref={(element => this.inputRef = element)}
                            elemAfterInput={(
                                <>
                                    {
                                        this.state.optionsOpen &&
                                        <Tooltip content={this.state.optionsOpen && 'Reset'} tag='div' position='left'>
                                            <ResetButtonContainer>
                                                <ClearButton
                                                    onClick={() => {
                                                        this.setState(
                                                            ({ previousSearchInputValue }) =>
                                                                ({ searchInputValue: previousSearchInputValue }),
                                                            this.inputRef.blur()
                                                        )
                                                    }}
                                                    isVisible={
                                                        this.state.previousSearchInputValue !== removeTrailingSlashFromPath(this.state.searchInputValue)
                                                    }
                                                />
                                            </ResetButtonContainer>
                                        </Tooltip>
                                    }
                                    <ArrowDownIcon
                                        className='material-icons-outlined'
                                        onClick={this.onClick}>
                                        keyboard_arrow_down
                                    </ArrowDownIcon>
                                    <AutosuggestionOptionsContainer
                                        className={`
                                            ${OPTIONS_CONTAINER_CLASS}
                                            ${this.state.optionsOpen && !this.state.options.length && 'autosuggestion-options--no-options'}
                                            ${this.state.loading && 'autosuggestion-options--loading'}
                                        `}
                                        ref={ref => this.optionsRef = ref}
                                        style={{
                                            'max-height': this.state.maxHeight,
                                            ...optionsStyles
                                        }}
                                    >
                                        <LoadingWrapper isLoading={this.state.loading}>
                                            {
                                                this.state.options.map(({ label, value, notClickable }, index) =>
                                                    <AutosuggestionOptionContainer
                                                        key={value}
                                                        onClick={() => !notClickable && this.selectOption(index)}
                                                        className={`
                                                            ${this.state.selectedOptionIndex === index ? SELECTED_OPTION_CLASS : ''}
                                                            ${notClickable ? 'autosuggestion-option--disabled' : ''}
                                                        `}
                                                    >
                                                        {label}
                                                    </AutosuggestionOptionContainer>
                                                )
                                            }
                                            {
                                                !this.state.options.length && !this.state.loading &&
                                                (typeof noOptionsMessage === 'function' ? noOptionsMessage(this.state.searchInputValue) : noOptionsMessage)
                                            }
                                        </LoadingWrapper>
                                    </AutosuggestionOptionsContainer>
                                </>
                            )}
                            theme={(current, props) => {
                                return {
                                    input: current(props).input,
                                    container: {
                                        ...current(props).container,
                                        ...styles
                                    }
                                }
                            }}
                        />
                    </AutosuggestionInputContainer>
                </AutosuggestionContainer>
            </>
        )
    }
}