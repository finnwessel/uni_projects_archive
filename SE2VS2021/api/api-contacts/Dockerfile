FROM mcr.microsoft.com/dotnet/aspnet:6.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src
COPY NuGet.Config /root/.nuget/NuGet/
COPY ["api-contact/api-contact.csproj", "api-contact/"]
RUN dotnet restore "api-contact/api-contact.csproj"
COPY . .
WORKDIR "/src/api-contact"
RUN dotnet build "api-contact.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "api-contact.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "api-contact.dll"]
