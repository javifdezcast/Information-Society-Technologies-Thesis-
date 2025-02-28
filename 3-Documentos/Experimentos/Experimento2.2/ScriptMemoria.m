% Define number of image sizes
num_sizes = 6; % Since we have ConsumoMemoriaJPG0 to ConsumoMemoriaJPG5

% Define different line styles for replicas
line_styles = {'-', '--', ':', '-.'};

% Create figure
figure; hold on;

% Initialize legend storage
legend_handles = gobjects(num_sizes, 1);
legend_labels = cell(num_sizes, 1);

% Activate left y-axis (Primary Replicas)
yyaxis left;
ylim([200 250]); % Set limits for primary replicas
ylabel('Consumo de Memoria (MB) - Principal');

% Loop through each dataset for main replicas
for i = 0:num_sizes-1
    % Construct variable name dynamically
    varName = sprintf('ConsumoMemoriaPNG%d', i);
    
    % Check if the variable exists
    if exist(varName, 'var')
        % Get the memory data
        data = eval(varName);
        
        % Plot first replica on left y-axis (No markers now)
        h = plot(Tiempo, data(:,1), 'Color', custom_colors(i+1,:), ...
                 'LineWidth', 1, 'LineStyle', '-','Marker', 'none');
        
        % Store for legend (one per image size)
        legend_handles(i+1) = h;
        legend_labels{i+1} = sprintf('Size %d', i);
    else
        warning('Variable %s not found.', varName);
    end
end

% Activate right y-axis (Secondary Replicas)
yyaxis right;
ylim([0 200]); % Set limits for secondary replicas
ylabel('Consumo de Memoria (MB) - Replicas');

% Loop again for secondary replicas
for i = 0:num_sizes-1
    varName = sprintf('ConsumoMemoriaPNG%d', i);
    
    if exist(varName, 'var')
        data = eval(varName);
        
        num_replicas = size(data, 2);
        
        for j = 2:num_replicas
            line_style = line_styles{mod(j-1, length(line_styles)) + 1}; % Cycle styles
            plot(Tiempo, data(:,j), 'Color', custom_colors(i+1,:), ...
                 'LineWidth', 1, 'LineStyle', line_style, 'Marker', 'none'); % No markers
        end
    end
end

% Labels and Titles
xlabel('Tiempo (s)');
title('Consumo de Memoria para Distintos Tamaños de Imagenes');

% Single Legend for Sizes Only
legend(legend_handles, legend_labels, 'Location', 'best');

grid on;
hold off;
