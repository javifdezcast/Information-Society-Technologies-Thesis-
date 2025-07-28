% Define the number of image sizes
num_sizes = 6; % Since we have ConsumoMemoriaJPG0 to ConsumoMemoriaJPG5

% Define different line styles for replicas
line_styles = {'-', '--', ':', '-.'};

% Hold figure for multiple plots
figure; hold on;

% Store legend handles and labels
legend_handles = gobjects(num_sizes, 1);
legend_labels = cell(num_sizes, 1);

% Loop through each dataset
for i = 0:num_sizes-1
    % Construct variable name dynamically
    varName = sprintf('CPUPNG%d', i);
    
    % Get the memory data
    data = eval(varName); 
    
    % Extract memory consumption values
    memory_values = data; 
    num_replicas = size(memory_values, 2); % Number of replicas

    % Plot each replica with the same color but different line styles
    for j = 1:num_replicas
        line_style = line_styles{mod(j-1, length(line_styles)) + 1}; % Cycle styles
        h = plot(Tiempo, memory_values(:,j), 'Color', custom_colors(i+1,:), 'LineWidth', 1, 'LineStyle', line_style);
        
        % Store the first handle for the legend
        if j == 1
            legend_handles(i+1) = h;
            legend_labels{i+1} = sprintf('Size %d', i);
        end
    end
end

% Customize the plot
xlabel('Tiempo (s)');
ylabel('Consumo de CPU (%)');
title('Consumo de CPU para Distintos Tamaños de Imagen');
legend(legend_handles, legend_labels, 'Location', 'best'); % Assign correct colors to legend
grid on;
hold off;
