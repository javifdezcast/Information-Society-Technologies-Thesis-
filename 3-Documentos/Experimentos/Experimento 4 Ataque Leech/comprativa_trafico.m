% Define X vector
X = 0:2:1202;

% Define data variables (assumes Base and BaseYMalicioso already exist)
Variables = [Base/2, BaseYMalicioso/2];
VarNames = {'Tráfico Base', 'Tráfico Malicioso'};  % Legend labels

Titles = "Comparación de Tráficos Base y Malicioso";
EjesY = "Número de Solicitudes";
EjeX = "Tiempo transcurrido (s)";

Colores = [
    "#0072bd";   % Blue for Base
    "#da5b24";   % Red for Malicious
];

% --- Create figure with more vertical space at top
fig = figure('Units', 'centimeters', 'Position', [5 5 16 10]);
set(fig, 'PaperUnits', 'centimeters', 'PaperPosition', [0 0 16 10]);

hold on;
h1 = plot(X, Variables(:,1), 'Color', Colores(1,:), 'LineWidth', 0.6);
h2 = plot(X, Variables(:,2), 'Color', Colores(2,:), 'LineWidth', 0.6);

% Add horizontal dotted line at Y = 125
% yline(62.5, '--k', 'LineWidth', 1.0);  % Black dotted line

% Axes settings
ax = gca;
set(ax, ...
    'FontSize', 9, ...
    'FontName', 'Times New Roman', ...
    'LineWidth', 0.6, ...
    'TickDir', 'out', ...
    'Box', 'on', ...
    'GridLineStyle', '--', ...
    'GridAlpha', 0.3);
grid on;

% Ensure Y=125 is shown explicitly
yticks = unique([get(ax, 'YTick'), 65]);  % Add 125 to existing Y ticks
yticks = sort(yticks);  % Sort for neatness
set(ax, 'YTick', yticks);

xlabel(EjeX, 'FontSize', 10, 'FontName', 'Times New Roman');
ylabel(EjesY, 'FontSize', 10, 'FontName', 'Times New Roman');

% Title with spacing and style
title(Titles, ...
    'FontSize', 14, ...
    'FontWeight', 'normal', ...
    'FontName', 'Times New Roman', ...
    'Units', 'normalized', ...
    'Position', [0.5, 1.02, 0]);

% Legend in bottom right with white background and black border
legend([h1 h2], VarNames, ...
    'FontSize', 9, ...
    'FontName', 'Times New Roman', ...
    'Location', 'southeast', ...
    'Box', 'on', ...
    'TextColor', 'black');
lgd = legend;
set(lgd, ...
    'Color', 'white', ...       % White background
    'EdgeColor', 'black');      % Black outline

% Set X-axis limits
xlim([min(X), max(X)]);

% Export as PNG (for Word)
filename = sprintf('comparativa_traficos.png');
print(fig, filename, '-dpng', '-r300');

close(fig);