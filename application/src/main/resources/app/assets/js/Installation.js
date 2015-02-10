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
        var Glyphicon = ReactBootstrap.Glyphicon;
        if (!this.state.data) {
            return (
                <div>
                    <Glyphicon glyph="refresh" className="glyphicon-refresh-animate" />
                </div>
            );
        }
        else {
            console.log(this.state.data.equipements);
            var equipements = this.state.data.equipements.map(function (equipement) {
                var activites = '';
                if (equipement.activites) {
                    activites = equipement.activites.join(' ; ');
                }
                return (
                    <tr>
                        <td>{equipement.numero}</td>
                        <td>{equipement.nom}</td>
                        <td>{equipement.type}</td>
                        <td>{equipement.famille}</td>
                        <td>{activites}</td>
                    </tr>
                );
            });
            console.log(equipements);
            return (
                <div className="well">
                    <p>
                        <b>{'\u0023 ' + this.state.data._id + ' - ' + this.state.data.nom}</b>
                    </p>
                    <p>
                        <Glyphicon glyph="envelope" />
                        {'\u00A0' + this.state.data.adresse.numero + ' ' + this.state.data.adresse.voie + ' ' + this.state.data.adresse.codePostal + ' ' + this.state.data.adresse.commune}
                    </p>
                    <p>
                        <Glyphicon glyph="map-marker" />
                        {'\u00A0 lat : ' + this.state.data.location.coordinates[0] + ' ; lon : ' + this.state.data.location.coordinates[1]}
                    </p>
                    <p>
                        <Glyphicon glyph="road" />
                        {'\u00A0' + this.state.data.nbPlacesParking + ' place(s) de parking'}
                    </p>
                    <p>
                        <b>Equipements</b>
                    </p>
                    <table className="table table-condensed">
                        <thead>
                            <tr>
                                <th>{'\u0023'}</th>
                                <th>Nom</th>
                                <th>Type</th>
                                <th>Famille</th>
                                <th>Activités</th>
                            </tr>
                        </thead>
                        <tbody>
                            {equipements}
                        </tbody>
                    </table>

                </div>
            );
        }
    }
});
