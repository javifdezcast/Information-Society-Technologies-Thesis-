% Define titles and labels
Titles = [
    "Consumo de CPU";
    "Consumo de Memoria";
    "Tasa de Invocación";
    "Réplicas de la Función";
    "Tiempo Medio de Procesamiento"
];

EjesY = [ 
    "Consumo de CPU (%)";
    "Consumo de Memoria (MB)";
    "Tasa de Invocación (uds/s)";
    "Número de Réplicas";
    "Tiempo de Procesamiento (s)"
];

EjeX = "Tiempo transcurrido (s)";

% Define custom line colors (MATLAB-like color order)
Colores = [
    "#0072bd";  % Blue
    "#da5b24";  % Red
    "#edb120";  % Yellow
    "#7e2f8e";  % Purple
    "#77ac30"   % Green
];

% Data grouping for A and D
Variables = {
    {CPU_A, Memoria_A, Invocaciones_A, Replicas_A, TiempoProcesamiento_A}, Tiempo_A, 'A';
    {CPU_D, Memoria_D, Invocaciones_D, Replicas_D, TiempoProcesamiento_D}, Tiempo_D, 'D'
};

for dataset = 1:2
    vars = Variables{dataset, 1};
    tiempo = Variables{dataset, 2};
    tag = Variables{dataset, 3};

    for i = 1:length(Titles)
        fig = figure('Units', 'centimeters', 'Position', [5 5 16 10]);
        set(fig, 'PaperUnits', 'centimeters', 'PaperPosition', [0 0 16 10]);
        hold on;

        data = vars{i};
        nSeries = size(data, 2);

        % Determine Y-axis limits
        yMax = max(data, [], 'all', 'omitnan');
        yLimit = yMax * 1.1;

        for j = 1:nSeries
            color = Colores(mod(j-1, size(Colores,1)) + 1, :);
            plot(tiempo, data(:, j), ...
                'Color', color, ...
                'LineStyle', '-', ...
                'LineWidth', 0.6);
        end

        % Axes styling
        ax = gca;
        set(ax, ...
            'FontSize', 9, ...
            'FontName', 'Calibri', ...
            'LineWidth', 0.6, ...
            'TickDir', 'out', ...
            'Box', 'on', ...
            'GridLineStyle', '--', ...
            'GridAlpha', 0.3);
        grid on;

        xlabel(EjeX, 'FontSize', 10, 'FontName', 'Calibri');
        ylabel(EjesY(i), 'FontSize', 10, 'FontName', 'Calibri');
        title(Titles(i), ...
            'FontSize', 14, ...
            'FontWeight', 'normal', ...
            'FontName', 'Calibri', ...
            'Units', 'normalized', ...
            'Position', [0.5, 1.02, 0]);

        % Legend
        if nSeries > 1
            labels = arrayfun(@(k) sprintf('Réplica %d', k), 1:nSeries, 'UniformOutput', false);
            legend(labels, ...
                'FontSize', 9, ...
                'FontName', 'Calibri', ...
                'Location', 'southeast', ...
                'Box', 'on', ...
                'TextColor', 'black', ...
                'LineWidth', 0.6);
            lgd = legend;
            set(lgd, 'Color', 'white', 'EdgeColor', 'black');
        end

        xlim([min(tiempo), max(tiempo)]);
        ylim([0, max(yLimit, 0.1)]);  % Avoid flat line if all values are zero

        % Save figure
        safe_title = lower(regexprep(char(Titles(i)), '\s+', '_'));
        filename = sprintf('grafico_%d_%s_%s.png', i, safe_title, tag);
        print(fig, filename, '-dpng', '-r300');
        close(fig);
    end
end
