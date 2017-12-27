import { QueriesResponse } from './api';

export interface State {
    requesting: boolean;
    response?: QueriesResponse
}
