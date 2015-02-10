var Installation = React.createClass({
    getInitialState: function () {
        return {};
    },
    componentDidMount: function () {
        $.ajax({
            url: this.props.url,
            dataType: 'json',
            success: function (data) {
                this.setState({data: data});
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    render: function () {
        if (!this.state.data) {
            return (
                <div>
                    <span className="glyphicon glyphicon-refresh glyphicon-refresh-animate"></span>
                </div>
            );
        }
        else {
            return (
                <div className="well">
                    <p>
                        <b>{'\u0023' + this.state.data._id + ' - ' + this.state.data.nom}</b>
                    </p>
                    <p>
                        <span className="glyphicon glyphicon-road">&nbsp;</span>
                        {this.state.data.adresse.numero + ' ' + this.state.data.adresse.voie + ' ' + this.state.data.adresse.codePostal + ' ' + this.state.data.adresse.commune}
                    </p>
                    <p>
                        <span className="glyphicon glyphicon-map-marker">&nbsp;</span>
                        {this.state.data.location.coordinates[0] + ' ; ' + this.state.data.location.coordinates[1]}
                    </p>
                </div>
            );
        }
    }
});
