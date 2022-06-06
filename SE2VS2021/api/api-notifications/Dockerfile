FROM mcr.microsoft.com/dotnet/aspnet:6.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src
COPY NuGet.Config /root/.nuget/NuGet/
COPY api-notifications/Plugins /app/plugins/

COPY ["api-notifications/api-notifications.csproj", "api-notifications/"]
RUN dotnet restore "api-notifications/api-notifications.csproj"
COPY . .
WORKDIR "/src/api-notifications"
RUN dotnet build "api-notifications.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "api-notifications.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=build /app/plugins /app/plugins
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "api-notifications.dll"]

# COPY api-notifications/Plugins/*.dll plugins
