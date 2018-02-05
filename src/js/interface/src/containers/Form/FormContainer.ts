import { State } from '../../state'
import { connect, Dispatch } from 'react-redux'
import { requestParse, setQuestion } from '../../actions'
import Form, { InputProps, OutputProps } from '../../components/Form/Form'

const mapStateToProps = ({requesting, question}: State): InputProps => ({
    value: question,
    requesting,
})

const mapDispatchToProps = (dispatch: Dispatch<State>): OutputProps => ({
    request: (question: string) =>
        dispatch(requestParse(question, true)),
    update: (question: string) =>
        dispatch(setQuestion(question))
})

export default connect(mapStateToProps, mapDispatchToProps)(Form)
