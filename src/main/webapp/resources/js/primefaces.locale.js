PrimeFaces.widget.Schedule.prototype._oldInit = PrimeFaces.widget.Schedule.prototype.init;
PrimeFaces.widget.Schedule.prototype.init = function (cfg){
        cfg.columnFormat = {
             month: 'ddd',
             week: 'ddd D/M',
             day: 'dddd D/M'
        };

    this._oldInit.apply(this, arguments);
};


PrimeFaces.locales['pt'] = {

            closeText: 'Fechar',
            prevText: 'Anterior',
            nextText: 'Próximo',
            currentText: 'Começo',
            monthNames: ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'],
            monthNamesShort: ['Jan','Fev','Mar','Abr','Mai','Jun', 'Jul','Ago','Set','Out','Nov','Des'],
            dayNames: ['Domingo','Segunda','Terça','Quarta','Quinta','Sexta','Sábado'],
            dayNamesShort: ['Dom','Seg','Ter','Qua','Qui','Sex','Sáb'],
            dayNamesMin: ['D','S','T','Q','Q','S','S'],
            weekHeader: 'Semana',
            firstDay: 1,
            isRTL: false,
            showMonthAfterYear: false,
            yearSuffix: '',
            timeOnlyTitle: 'Só Horas',
            timeText: 'Tempo',
            hourText: 'Hora',
            minuteText: 'Minuto',
            secondText: 'Segundo',
            currentText: 'Data Atual',
            ampm: false,
            month: 'Mês',
            week: 'Semana',
            day: 'Dia',
            allDayText : 'Todo Dia',
            titleFormat : {
                month : "MMMM yyyy",
                week : "MMM d[ yyyy]{ '&#8212;'[ MMM] d yyyy}",
                day : "dddd, MMM d, yyyy"
            },
            
            timeFormat : {
                "" : "h(:mm)t"
            }
};