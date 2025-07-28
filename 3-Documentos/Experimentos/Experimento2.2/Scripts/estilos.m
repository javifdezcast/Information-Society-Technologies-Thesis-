% Load the figure
fig = openfig('C:\Users\jfdez\Information-Society-Technologies-Thesis-\3-Documentos\Experimentos\Experimento2.2\Tiempo200PNG.fig', 'invisible');  % Load invisibly

% Apply figure size properties
set(fig, 'Units', 'centimeters', 'Position', [5 5 16 10]);
set(fig, 'PaperUnits', 'centimeters', 'PaperPosition', [0 0 16 10]);

% Get all axes in the figure
axesHandles = findall(fig, 'Type', 'axes');

% Define 6 line colors
Colores = [
    "#0072bd";  % Blue
    "#da5b24";  % Red
    "#edb120";  % Yellow
    "#7e2f8e";  % Purple
    "#77ac30";  % Green
    "#747474"   % Grey
];

% Style settings
for ax = axesHandles'
    % Format axes
    set(ax, ...
        'FontSize', 9, ...
        'FontName', 'Calibri Light', ...
        'LineWidth', 0.6, ...
        'TickDir', 'out', ...
        'Box', 'on', ...
        'GridLineStyle', '--', ...
        'GridAlpha', 0.3);
    grid(ax, 'on');

    % Format lines in the axes
    lines = findall(ax, 'Type', 'line');
    for j = 1:length(lines)
        set(lines(j), ...
            'LineWidth', 0.6);
    end

    % Format text (titles, labels, etc.)
    set(get(ax, 'XLabel'), 'FontSize', 10, 'FontName', 'Calibri');
    set(get(ax, 'YLabel'), 'FontSize', 10, 'FontName', 'Calibri');

    % Format title
    titleHandle = get(ax, 'Title');
    set(titleHandle, ...
        'FontSize', 14, ...
        'FontWeight', 'normal', ...
        'FontName', 'Calibri', ...
        'Units', 'normalized', ...
        'Position', [0.5, 1.02, 0]);

    % Format legend if present
    lgd = findobj(fig, 'Type', 'legend');
    for l = lgd'
        set(l, ...
            'FontSize', 5, ...
            'FontName', 'Calibri Light', ...
            'Box', 'on', ...
            'TextColor', 'black', ...
            'LineWidth', 0.6, ...
            'Color', 'white', ...
            'EdgeColor', 'black', ...
            'Location', 'southeastoutside');

%        'Location', 'southeast', ...
 %       pos = get(l, 'Position');
  %      diff = pos(1)*1.06 - pos(1);
   %     pos(1) = pos(1)*1.06;  
    %    pos(3) = pos(3) - diff;
     %   set(l, 'Position', pos);
    end
end

% Save the modified figure (overwrite or rename as needed)
print(fig, 'T_PNG_styled.png', '-dpng', '-r300');
savefig(fig, 'T_PNG_styled.fig');

close(fig);  % Optional: close after saving
