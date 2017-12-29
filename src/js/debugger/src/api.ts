import axios from 'axios';
import { Parse } from './types';

export type Cancel = (message?: string) => void;

export const parse = (question: string):
    [Promise<Parse>, Cancel] => {

    const source = axios.CancelToken.source();

    const promise = axios.get('/api/parse', {
        params: {sentence: question},
        cancelToken: source.token
    })
        .then(response => Parse.decode(response.data))
        .catch(e => {
            if (axios.isCancel(e)) {
                return;
            }

            throw e;
        }) as Promise<Parse>;

    return [
        promise,
        source.cancel.bind(source)
    ];
};
