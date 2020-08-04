$('#insertSensor_btn').click(function () {
    $('insertSensor_form').submit();
})
$('#describeSensor_btn').click(function () {
    $('#describeSensor_form').submit();
    // $('#describeSensor').load("/sensor/describeSensor");
})

$('#insertObservation_btn').click(function () {
    $('#insertObservation_form').submit();
})

$('#getObservation_btn').click(function () {
    $('#getObservation_form').submit();
})

$('#getCapabilities_btn').click(function () {
    $('#getCapabilities_form').submit();
})

function show() {
    var option = $('#selectContent').val();
    if (option==="insertSensor") {
        $('#insertSensor').show();
        $('#describeSensor').hide();
        $('#deleteSensor').hide();
        $('#insertObservation').hide();
        $('#getObservation').hide();
        $('#getCapabilities').hide();

    } else if (option==="describeSensor") {
        $('#insertSensor').hide();
        $('#describeSensor').show();
        $('#deleteSensor').hide();
        $('#insertObservation').hide();
        $('#getObservation').hide();
        $('#getCapabilities').hide();
    } else if(option==="deleteSensor") {
        $('#insertSensor').hide();
        $('#describeSensor').hide();
        $('#deleteSensor').show();
        $('#insertObservation').hide();
        $('#getObservation').hide();
        $('#getCapabilities').hide();
    } else if (option==="insertObservation") {
        $('#insertSensor').hide();
        $('#describeSensor').hide();
        $('#deleteSensor').hide();
        $('#insertObservation').show();
        $('#getObservation').hide();
        $('#getCapabilities').hide();
    } else if (option==="getObservation") {
        $('#insertSensor').hide();
        $('#describeSensor').hide();
        $('#deleteSensor').hide();
        $('#insertObservation').hide();
        $('#getObservation').show();
        $('#getCapabilities').hide();
    } else {
        $('#insertSensor').hide();
        $('#describeSensor').hide();
        $('#deleteSensor').hide();
        $('#insertObservation').hide();
        $('#getObservation').hide();
        $('#getCapabilities').show();
    }
}
