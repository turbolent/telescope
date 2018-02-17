import { State } from '../../state'
import { connect } from 'react-redux'
import Status, { Props } from '../../components/Status/Status'

const mapStateToProps = ({error}: State): Props => ({
    message: error !== undefined ? 'Something went wrong' : undefined
})

export default connect(mapStateToProps)(Status)
