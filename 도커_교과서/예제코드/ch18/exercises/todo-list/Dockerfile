FROM diamol/dotnet-sdk AS builder

WORKDIR /src
COPY src/ToDoList.csproj .
RUN dotnet restore

COPY src/ .
RUN dotnet publish -c Release -o /out ToDoList.csproj

# app image
FROM diamol/dotnet-aspnet

ENV DOTNET_ENVIRONMENT="Development"
WORKDIR /app
ENTRYPOINT ["dotnet", "ToDoList.dll"]

COPY --from=builder /out/ .