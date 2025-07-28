Variables = {A, CPU, M, I, R, T};
VarNames = {'A', 'CPU', 'M', 'I', 'R', 'T'};

Titles = [
    "Solicitudes Recibidas en los Últimos 10 Minutos";
    "Consumo de CPU";
    "Consumo de Memoria";
    "Tasa de Invocación";
    "Réplicas de la Función";
    "Tiempo Medio de Procesamiento"];

EjesY = [
    "Número de Solicitudes";
    "Consumo de CPU (%)";
    "Consumo de Memoria (MB)";
    "Tasa de Invocación (uds/s)";
    "Número de Réplicas";
    "Tiempo de Procesamiento (s)"];

Posiciones = [
    "northeast";
    "northeast";
    "southeast";
    "northeast";
    "southeast";
    "northeast"];



EjeX = "Tiempo transcurrido (s)";

Colores = [
    "#9ed8ff";  % Light blue for individual lines
    "#0072bd"   % Dark blue for average
];

for i = 1:numel(Variables)
    Y = Variables{i};

    % Check if current variable is 'A' or 'I'
    if strcmp(VarNames{i}, 'A') || strcmp(VarNames{i}, 'I')
        Y = Y / 2;
    end

    % --- Create figure with more vertical space at top
    fig = figure('Units', 'centimeters', 'Position', [5 5 16 10]);
    set(fig, 'PaperUnits', 'centimeters', 'PaperPosition', [0 0 16 10]);

    hold on;
    for j = 1:size(Y,2)
        plot(X, Y(:,j), 'Color', Colores(1,:), 'LineWidth', 0.6);
    end
    avg = mean(Y, 2);
    plot(X, avg, 'Color', Colores(2,:), 'LineWidth', 0.8);

    % Axes settings
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

    xlim([min(X), max(X)]);

    xlim([min(X), max(X)]);

    h1 = plot(nan, nan, 'Color', "#9ed8ff", 'LineWidth', 1.2);  % Light blue
    h2 = plot(nan, nan, 'Color', "#0072bd", 'LineWidth', 1.2);  % Dark blue
    
    legend([h1, h2], {'Iteraciones', 'Media'}, ...
        'FontSize', 9, ...
        'FontName', 'Calibri Light', ...
        'Location', Posiciones(i), ...
        'Box', 'on', ...
        'TextColor', 'black', ...
        'LineWidth', 0.6, ...
        'Color', 'white', ...
        'EdgeColor', 'black');

    filename = sprintf('fig_%s.png', VarNames{i});
    print(fig, filename, '-dpng', '-r300');

    close(fig);
end
