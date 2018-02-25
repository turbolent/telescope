import axios from 'axios'
import { decodeResults, Parse, Result, WikipediaPreview } from './types'

const PARSE_API_PATH = '/api/parse'
const QUERY_API_URL = 'https://query.wikidata.org/bigdata/namespace/wdq/sparql'
const WIKIPEDIA_API_BASE_URL = 'https://en.wikipedia.org/api/rest_v1'

export type Cancel = (message?: string) => void

export const parse = (question: string):
    [Promise<Parse>, Cancel] => {

    const source = axios.CancelToken.source()

    const params = {
        sentence: question,
        result: 'queries',
        label: true,
        wikipediaTitle: true,
        wikipediaTitleOptional: true
    }

    const promise = axios.get(PARSE_API_PATH, {
        params,
        cancelToken: source.token
    })
        .then(response => Parse.decode(response.data))
        .catch(e => {
            if (axios.isCancel(e)) {
                return
            }

            throw e
        }) as Promise<Parse>

    return [
        promise,
        source.cancel.bind(source)
    ]
}

export const search = (query: string):
    [Promise<Result[]>, Cancel] => {

    const source = axios.CancelToken.source()

    const promise = axios.get(QUERY_API_URL, {
        params: {query},
        cancelToken: source.token,
        headers: {'accept': 'application/sparql-results+json'}
    })
        .then(response => decodeResults(response.data))
        .catch(e => {
            if (axios.isCancel(e)) {
                return
            }

            throw e
        }) as Promise<Result[]>

    return [
        promise,
        source.cancel.bind(source)
    ]
}

export const requestPreview = (wikipediaTitle: string):
    [Promise<WikipediaPreview>, Cancel] => {

    const source = axios.CancelToken.source()

    const url = WIKIPEDIA_API_BASE_URL + '/page/summary/'
        + encodeURIComponent(wikipediaTitle)
    const promise = axios.get(url, {
        cancelToken: source.token,
        headers: {'accept': 'application/json'}
    })
        .then(response => WikipediaPreview.decode(response.data))
        .catch(e => {
            if (axios.isCancel(e)) {
                return
            }

            throw e
        }) as Promise<WikipediaPreview>

    return [
        promise,
        source.cancel.bind(source)
    ]
}
