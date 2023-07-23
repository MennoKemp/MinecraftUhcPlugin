library(shiny)
library(ggplot2)

#srhinkDuration = 180 - 60
#shrinkAmount = 150 - 2000
#finalSpeed = 0.5 * 60

#b <- input$shrink_start
#d <- input$radius_initial

#c <- finalSpeed * srhinkDuration / shrinkAmount
#a <- -finalSpeed/(c*srhinkDuration^(c - 1))

#x <- seq(input$shrink_start, input$shrink_end, length.out = 100)
#y <- a*(x-b)^c+d
#data <- data.frame(x, y)

#ggplot(data, aes(x, y)) +
#  geom_line(color = "blue", size = 1) +
#  scale_x_continuous(breaks = seq(input$shrink_start, input$shrink_end, len=5)) + 
#  labs(x = "Time [min]", y = "World Radius [m]")




ui <- fluidPage(
  titlePanel("Shrinking World Border"),
  sidebarLayout(
    sidebarPanel(
      sliderInput(inputId = "shrink_start", label = "Shrink Start [min]", min = 0, max = 300, value = 60, step = 30),
      sliderInput(inputId = "shrink_end", label = "Shrink End [min]", min = 0, max = 300, value = 180, step = 30),
      sliderInput(inputId = "radius_initial", label = "Initial Radius [m]", min = 0, max = 5000, value = 2000, step = 50),
      sliderInput(inputId = "radius_final", label = "Final Radius [m]", min = 0, max = 5000, value = 150, step = 50),
      sliderInput(inputId = "speed_final", label = "Final Speed [m/s]", min = 0, max = 2, value = 0.5, step = 0.1),
      ),
    mainPanel(
      plotOutput(outputId = "radius_plot")
    )
  )
)

server <- function(input, output) {
  output$radius_plot <- renderPlot({
    srhinkDuration = input$shrink_end - input$shrink_start
    shrinkAmount = input$radius_final - input$radius_initial
    finalSpeed = input$speed_final * 60
    
    b <- input$shrink_start
    d <- input$radius_initial
    
    c <- -finalSpeed * srhinkDuration / shrinkAmount
    a <- -finalSpeed/(c*srhinkDuration^(c - 1))
    
    print(c)
    
    x <- seq(input$shrink_start, input$shrink_end, length.out = 100)
    y <- a*(x-b)^c+d
    data <- data.frame(x, y)
    
    ggplot(data, aes(x, y)) +
      geom_line(color = "blue", size = 1) +
      scale_x_continuous(breaks = seq(input$shrink_start, input$shrink_end, len=5)) + 
      labs(x = "Time [min]", y = "World Radius [m]")
  })
}

shinyApp(ui = ui, server = server)
