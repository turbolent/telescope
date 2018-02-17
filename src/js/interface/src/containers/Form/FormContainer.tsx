import { State } from '../../state'
import { connect, Dispatch } from 'react-redux'
import { requestParse, saveQuestion, setQuestion } from '../../actions'
import Form, { InputProps, OutputProps } from '../../components/Form/Form'

const mapStateToProps = ({requesting, question, results}: State): InputProps => ({
    value: question,
    requesting,
    empty: !question || !question.length || !results || !results.length
})

const mapDispatchToProps = (dispatch: Dispatch<State>): OutputProps => ({
    request: (question: string) =>
        dispatch(requestParse(question)),
    update: (question: string) =>
        dispatch(setQuestion(question)),
    save: (question: string) =>
        dispatch(saveQuestion(question))
})

export default connect(mapStateToProps, mapDispatchToProps)(Form)
